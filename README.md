### What about the app?

The app is the blackbox testing to test AIQUA SDK integration. It uses Kotlin Multiplatform technology (KMP)
So the core logic can be shared across iOS & Android. The UI stays native with Compose (Android) and SwiftUI (iOS). 

The architecture of the app is documented at [docs/architecture.md](./docs/architecture.md) and for how data
flows through the app, and [docs/adr](./docs/adr) for key decisions.

### Screenshots

Native Android and iOS screens, captured from the running apps.

| Android | iOS |
| --- | --- |
| <img src="docs/screenshots/android/home.png" alt="Android home product catalog" width="260"> | <img src="docs/screenshots/ios/home.png" alt="iOS home product catalog" width="260"> |

#### Home

Browse grouped products, add items, and adjust quantities without leaving the catalog.

| State | Android | iOS |
| --- | --- | --- |
| Product catalog | <img src="docs/screenshots/android/home.png" alt="Android home showing product categories and prices" width="240"> | <img src="docs/screenshots/ios/home.png" alt="iOS home showing product categories and prices" width="240"> |
| Selected products and quantities | <img src="docs/screenshots/android/home-selected.png" alt="Android home with selected products, quantity controls, and cart badge" width="240"> | <img src="docs/screenshots/ios/home-selected.png" alt="iOS home with selected products, quantity controls, and cart badge" width="240"> |

#### Cart

The empty state links back to shopping. A filled cart shows line totals, quantity controls,
the order total, and the Buy action.

| State | Android | iOS |
| --- | --- | --- |
| Empty cart | <img src="docs/screenshots/android/cart-empty.png" alt="Android empty cart with Start shopping action" width="240"> | <img src="docs/screenshots/ios/cart-empty.png" alt="iOS empty cart with Start shopping action" width="240"> |
| Filled cart | <img src="docs/screenshots/android/cart-filled.png" alt="Android cart with Butter, Cheese, Eggs, and order total" width="240"> | <img src="docs/screenshots/ios/cart-filled.png" alt="iOS cart with Butter, Cheese, Eggs, and order total" width="240"> |

#### Checkout

Checkout blocks further interaction while placing an order. Success clears the cart;
failure preserves its contents so the user can try again.

| State | Android | iOS |
| --- | --- | --- |
| Placing an order | <img src="docs/screenshots/android/order-progress.png" alt="Android checkout progress overlay" width="240"> | <img src="docs/screenshots/ios/order-progress.png" alt="iOS checkout progress overlay" width="240"> |
| Order placed | <img src="docs/screenshots/android/order-success.png" alt="Android order-success confirmation and total" width="240"> | <img src="docs/screenshots/ios/order-success.png" alt="iOS order-success confirmation and total" width="240"> |
| Order failed | <img src="docs/screenshots/android/order-failure.png" alt="Android order-failure message with cart retained" width="240"> | <img src="docs/screenshots/ios/order-failure.png" alt="iOS order-failure message with cart retained" width="240"> |

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
