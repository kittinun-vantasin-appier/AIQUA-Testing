## What is this app?

The app is the blackbox testing to test AIQUA SDK integration. It uses Kotlin Multiplatform
technology (KMP).
So the core logic (Service, Repository, ViewModel) can be shared across iOS & Android including the AIQUA's installation, event
logging, etc. However, the UI stays native with Compose (Android) and SwiftUI (iOS) to be closer to
the UI for each platform.

The same app is also built in React Native ([/reactNativeApp](./reactNativeApp)) to test AIQUA's React Native SDK the
way a React Native customer would integrate it. It has its own TypeScript logic and shares only the Catalog
(`product.json`) with the native apps ([ADR-0005](./docs/adr/0005-react-native-host-app.md)).

The architecture of the app is documented at [docs/architecture.md](./docs/architecture.md) and for
how data flows through the app, and [docs/adr](./docs/adr) for key decisions for all technical
decisions in building the app.

### AIQUA events

These are the events the app sends to AIQUA. Both apps send the same events from the shared
ViewModels (`sharedLogic`), so Android and iOS always match. Every 5th Order fails on purpose, so
`checkout_failed` is easy to trigger. The React Native app sends the same events, except the inbox ones
(`screen_name` `inbox`, `qg_inapp_displayed`, `qg_inapp_clicked`): AIQUA's React Native SDK has no inbox.

Every event the apps log themselves (all except `qg_inapp_*`) also carries **`host`**: `android`, `ios` or
`react_native`, the app that sent it, because the dashboard only splits activity by Android and iOS
([ADR-0006](./docs/adr/0006-host-event-parameter.md)). The SDK also logs `qg_inapp_displayed` / `qg_inapp_closed` on
its own for popup campaigns, in every app, without `host`.

| Event                | Parameters                                                                                                               | Sent when                                                                                                                                                      |
|----------------------|--------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `screen_viewed`      | `screen_name`: `home` / `cart` / `inbox`; `cart_unit_count` on cart views                                                | A screen appears: app launch or a tab switch.|
| `cart_added`         | `product_id`, `product_name`, `category`, `price` (one unit, yen), `quantity` (1), `product_quantity`, `cart_unit_count` | A successful addition from Home or Cart. Counts reflect the updated cart; no event when already at the quantity limit.                       |
| `product_purchased`  | `order_id`, `product_id`, `product_name`, `category`, `price` (one unit, yen), `quantity`                                | An Order was placed: one event per item, before `checkout_completed`. No value.                                                                                |
| `checkout_completed` | `order_id`, `product_count`, **valueToSum** = Order total, **valueToSumCurrency** = `JPY` (hardcoded as JPY for now :P)  | An Order was placed. The only event with a value, so revenue is counted once.                                                                                  |
| `checkout_failed`    | `reason` (the error message)                                                                                             | Placing an Order failed.                                                                                                      |
| `qg_inapp_displayed` | none (the SDK adds the message's `notificationId`)                                                                       | The inbox sheet opens: one per message in it. AIQUA counts these as the inbox campaign's impressions.                                                          |
| `qg_inapp_clicked`   | none (the SDK adds the message's `notificationId`)                                                                       | A message in the Inbox is tapped, every time. AIQUA counts these as clicks. The message is marked read.                                                        |

Events sharing an `order_id` belong to the same Order. To see them, open the AIQUA dashboard, then
Settings >
Recent activity (Android / iOS tabs); they can take a few minutes to appear. How events are wired is
in
[docs/architecture.md](./docs/architecture.md#events).

#### In-App Campaign

|                     | Android                                                                   | iOS                                                                   |
|---------------------|---------------------------------------------------------------------------|-----------------------------------------------------------------------|
| Popup campaign_cart | <img src="docs/screenshots/android/in-app-campaign_cart.png" width="240"> | <img src="docs/screenshots/ios/in-app-campaign_cart.png" width="240"> |
| Inbox campaign_home | <img src="docs/screenshots/android/inbox-campaign_home.png" width="240">  | <img src="docs/screenshots/ios/inbox-campaign_home.png" width="240">  |

### Screenshots

Native Android and iOS screens, captured from the running apps.

| Android                                                                                      | iOS                                                                                  |
|----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| <img src="docs/screenshots/android/home.png" alt="Android home product catalog" width="260"> | <img src="docs/screenshots/ios/home.png" alt="iOS home product catalog" width="260"> |

#### Home

Browse grouped products, add items, and adjust quantities without leaving the catalog.

| State                            | Android                                                                                                                                         | iOS                                                                                                                                     |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| Product catalog                  | <img src="docs/screenshots/android/home.png" alt="Android home showing product categories and prices" width="240">                              | <img src="docs/screenshots/ios/home.png" alt="iOS home showing product categories and prices" width="240">                              |
| Selected products and quantities | <img src="docs/screenshots/android/home-selected.png" alt="Android home with selected products, quantity controls, and cart badge" width="240"> | <img src="docs/screenshots/ios/home-selected.png" alt="iOS home with selected products, quantity controls, and cart badge" width="240"> |

#### Cart

The empty state links back to shopping. A filled cart shows line totals, quantity controls, the
order total, and the Buy action.

| State       | Android                                                                                                                        | iOS                                                                                                                    |
|-------------|--------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| Empty cart  | <img src="docs/screenshots/android/cart-empty.png" alt="Android empty cart with Start shopping action" width="240">            | <img src="docs/screenshots/ios/cart-empty.png" alt="iOS empty cart with Start shopping action" width="240">            |
| Filled cart | <img src="docs/screenshots/android/cart-filled.png" alt="Android cart with Butter, Cheese, Eggs, and order total" width="240"> | <img src="docs/screenshots/ios/cart-filled.png" alt="iOS cart with Butter, Cheese, Eggs, and order total" width="240"> |

#### Checkout

Checkout blocks further interaction while placing an order. Success clears the cart; failure
preserves its contents so the user can try again.

| State            | Android                                                                                                                   | iOS                                                                                                               |
|------------------|---------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| Placing an order | <img src="docs/screenshots/android/order-progress.png" alt="Android checkout progress overlay" width="240">               | <img src="docs/screenshots/ios/order-progress.png" alt="iOS checkout progress overlay" width="240">               |
| Order placed     | <img src="docs/screenshots/android/order-success.png" alt="Android order-success confirmation and total" width="240">     | <img src="docs/screenshots/ios/order-success.png" alt="iOS order-success confirmation and total" width="240">     |
| Order failed     | <img src="docs/screenshots/android/order-failure.png" alt="Android order-failure message with cart retained" width="240"> | <img src="docs/screenshots/ios/order-failure.png" alt="iOS order-failure message with cart retained" width="240"> |

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these
commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.
- React Native app, from [/reactNativeApp](./reactNativeApp) (needs Node and CocoaPods): `npm install`, then
  `cd ios && bundle install && bundle exec pod install`, then `npx react-native start` and, in another terminal,
  `npx react-native run-android` or `npx react-native run-ios`. Tests: `npx jest`.

---

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
