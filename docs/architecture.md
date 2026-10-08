# Architecture

How data moves through the app. For the domain vocabulary (Catalog, Cart, Cart Line, Order, …) see
[CONTEXT.md](../CONTEXT.md); for why things are shaped this way see [docs/adr](./adr).

## The map

```
                        UI  (Compose on Android / SwiftUI on iOS)
              renders UiState ▲        │ taps: onAddClick(product), onBuyClick(), …
                              │        ▼
  ┌───────────────────── sharedLogic (Kotlin, both platforms) ─────────────────────┐
  │                                                                                │
  │  ViewModels       MainViewModel     HomeViewModel          CartViewModel       │
  │  screen state     (tab badge)            │      └─────────┐     │              │
  │                        │                 ▼                ▼     ▼              │
  │  Repositories          │           HomeRepository       CartRepository         │
  │  hold state            └──────────────────────────────▶ (the one Cart)         │
  │  (interfaces)                      products: StateFlow  cart: StateFlow        │
  │                                    refresh()            add / decrement /      │
  │                                          │              remove / checkout()    │
  │                                          ▼                    │                │
  │  Services                          CatalogService       OrderService           │
  │  stateless                         reads product.json   places an Order        │
  │  (interfaces)                      (fake, 500 ms)       (fake, 2 s, every 5th  │
  │                                                          attempt fails)        │
  └────────────────────────────────────────────────────────────────────────────────┘
        arrows = "uses"; state comes back up as StateFlows
        Android observes with collectAsStateWithLifecycle(), iOS with asyncSequence(for: …Flow)
```

**Rule 1: data flows one way.** The UI never calculates anything. It renders a `UiState` and reports taps back as
plain function calls. All text formatting (e.g. `¥1,280`) happens in `sharedLogic`, so both platforms show exactly the
same thing.

**Rule 2: each layer only talks to the one below it** ([ADR-0003](./adr/0003-repositories-hold-state-services-are-stateless.md)).
The UI only sees ViewModels, even for the tab badge, and ViewModels never see a Service. Repositories and Services are
interfaces, so tests can swap in fakes.

**Rule 3: analytics events come from ViewModels** ([ADR-0004](./adr/0004-analytics-events-come-from-viewmodels.md)).
That's where the screen context is.

## The pieces

### Services (stateless)

- **`CatalogService`** / `JsonCatalogService`: fetches the Products from `product.json` after 500 ms of fake latency.
  The JSON is a moko-resources file (`MR.files.product_json`), read inside `sharedLogic` by `readProductJson()`:
  iOS reads it from the bundle; Android needs a `Context`, which sharedLogic captures itself at process start with a
  tiny internal `AppContextProvider` (a `ContentProvider` declared in its manifest). So both apps just call
  `AppContainer()`.
- **`OrderService`** / `FakeOrderService`: places an Order after 2 s of fake latency with an Order ID such as
  `ORD-482913`. **Every 5th attempt (5th, 10th, … in each app run) throws `OrderFailedException`**, so a failure can be
  reproduced on demand. The attempt counter simulates the backend, not app state.

### Repositories (stateful)

- **`HomeRepository`** / `DefaultHomeRepository`: holds the Home content (`products`, null until the first load).
  `refresh()` is the one place Home content is (re)loaded. It's ready for pull-to-refresh, and it's where an AIQUA
  "content loaded" event would be triggered (from the ViewModel that called it).
- **`CartRepository`** / `DefaultCartRepository`: owns the one in-memory Cart and its rules. Cart Lines stay in the
  order they were first added, each holds 1–9 units, and taking away the last unit removes the Cart Line.
  `checkout()` places an Order through `OrderService`, then **takes exactly the ordered units out of the Cart**, so a
  change made while the Order was being placed isn't lost. If the Order fails, the Cart is untouched.

### Plain data

- **`Product`**, **`Cart`** / **`CartLine`**, **`Order`**: immutable values. `Cart.total` and `Cart.unitCount` are
  always derived, never stored.

### Wiring

- **`AppContainer`** creates the Services, then the Repositories on top of them, exactly once, and hands out
  ViewModels. Android keeps one in `GroceryApplication`; iOS keeps one in `iOSApp`. The Repositories are private;
  the outside world only gets ViewModels.

## ViewModels: inputs in, one state out

| ViewModel | Inputs | Output |
|---|---|---|
| `MainViewModel` | `cartRepository.cart` | `MainUiState(cartBadgeText)`: null when empty, "99+" past 99 |
| `HomeViewModel` | `homeRepository.products`, `cartRepository.cart`, its own load state (loading / error) | `HomeUiState(isLoading, error, sections)` |
| `CartViewModel` | `cartRepository.cart`, its own Order state (placing / placed / failed) | `CartUiState(lines, totalText, isPlacingOrder, placedOrderTotalText, orderError)` |

`HomeViewModel` groups Products by Category, sorts Categories and Products A→Z, uppercases the headers and attaches
each Product's quantity from the Cart.

Every `UiState` is a data class of independent fields rather than a sealed one-of-several hierarchy, so combinations
such as "showing sections while refreshing" or "showing sections after a failed refresh" can be expressed. On Home,
both UIs follow one rule: **content wins.** If there are sections, show them; otherwise show the spinner while
loading, or the error with Retry.

## Buying, step by step

```
t=0s   CartViewModel.onBuyClick(): ignore if the Cart is empty or the overlay is already up
       isPlacingOrder = true                          ▶ spinner covers the whole app
       cartRepository.checkout()
         └─ orderService.placeOrder(cart)             (2 s)
t=2s   ├─ success: ordered units leave the Cart       ▶ badge drops, empty Cart behind the overlay
       │           placedOrderTotalText = "¥…"        ▶ ✅ Order placed ¥…
       └─ failure: Cart untouched                     (every 5th attempt)
                   orderError = the exception         ▶ ❌ Order failed, "Your cart is still here"
t=4s   all three cleared                              ▶ overlay closes; Buy again to retry
```

While any of the three is set, `CartUiState.isOrderOverlayVisible` is true and the overlay blocks all input
(Android: a full-screen `Dialog`; iOS: a `fullScreenCover`).

## Shared resources (moko-resources)

All UI text, the colour palette (light and dark) and `product.json` live once in
`sharedLogic/src/commonMain/moko-resources/` (`base/strings.xml`, `colors/colors.xml`, `files/`). moko generates `MR`
in `com.github.kittinunf.aiqua_testing.resources` and builds native resources for each platform.

| | Android | iOS |
|---|---|---|
| Text | `stringResource(MR.strings.x.resourceId)` | `Text(MR.strings.shared.x)` (helper in `SharedResources.swift`) |
| Colour | `GroceryTheme` reads every role with `colorResource(...)` | `Color(MR.colors.shared.x)` |
| Packaging | Android resources in the library, merged into the APK | resource bundle copied into the app by the "Copy Kotlin Framework Resources" build phase (the framework is static) |

Only `moko:resources` is exported to Swift, not `moko:graphics`: its `Color` class would clash with SwiftUI's `Color`.

## AIQUA SDK

`Aiqua` is an `expect object` in `sharedLogic` (`aiqua/`) with one `actual object` per platform over the native SDK.
It keeps no state: the SDK holds its own single client, which `Aiqua` asks for when it needs it (Android:
`QG.getInstance(AppContextProvider.appContext)`, the same captured Context the catalog uses; iOS:
`QGSdk.getSharedInstance()`). Each platform has its own `init` (Android needs the `Application`, iOS doesn't):
`Aiqua.init(application = this)` in `GroceryApplication.onCreate`, `Aiqua.shared.configure()` in the iOS `App`'s `init`
(`init` is reserved in Swift). The app ID is `Constants.APP_ID`.

| | Android | iOS |
|---|---|---|
| SDK | `com.appier:appier-android` as a `sharedLogic` androidMain dependency | `AppierFramework` (Swift Package Manager) linked by the iOS app |
| Kotlin calls | `QG.initializeSdk(application, appId)`; verbose SDK logs in debug builds | `QGSdk.getSharedInstance().onStart(appId)` through Kotlin bindings generated from the SDK's Objective-C headers (`appier.def`; Gradle downloads the headers of the same version) |
| Version pin | `appier-android` in `libs.versions.toml` | `appier-ios` in `libs.versions.toml` **and** the Swift package's exact version in Xcode: keep them equal |

Firebase isn't set up; the SDK uses its own internal Firebase instance, so events (and even push tokens) work without
a `google-services.json`.

### Events

ViewModels send events ([ADR-0004](./adr/0004-analytics-events-come-from-viewmodels.md)) through `EventLogger`, a
one-method interface that `Aiqua` implements (`AppContainer()` passes it to the ViewModels); tests use a fake. Event names and parameter keys live in `EventLogger.kt`.

| Event | Parameters | Sent when |
|---|---|---|
| `screen_viewed` | `screen_name`: `home` / `cart` | The screen appears. A ViewModel can't see that, so the UI calls `onScreenViewed()`: Android from a `LaunchedEffect`, iOS from `.onAppear`. That covers app launch and tab switches; on Android a rotation re-sends it, because the screen is composed again. |

## Platform glue

| | Android | iOS |
|---|---|---|
| Container created once | `GroceryApplication` | `iOSApp` |
| ViewModel lifetime | Navigation 3 back stack entry (`rememberViewModelStoreNavEntryDecorator`); `MainViewModel` lives with the Activity | `ViewModelOwner`, cleared in `Store.deinit` |
| Observing state | `collectAsStateWithLifecycle()` | `Store` loops over `asyncSequence(for: uiStateFlow)` for its whole lifetime (KMP-NativeCoroutines) |
| Cart tab badge | `MainViewModel.uiState` | `MainViewModel.uiStateFlow` |
| Navigation | One back stack: `[Home]` or `[Home, Cart]` | `TabView` (both tabs stay alive) |

## Known gaps

- **The Cart is in memory only.** Killing the app empties it. This is by design for now.
- **The failure counter resets with the app.** The 5th attempt fails in each app run, not across runs.
