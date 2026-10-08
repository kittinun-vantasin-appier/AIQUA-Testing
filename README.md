### What about the app?

The app is the blackbox testing to test AIQUA SDK integration. It uses Kotlin Multiplatform technology (KMP)
So the core logic can be shared across iOS & Android. The UI stays native with Compose (Android) and SwiftUI (iOS). 

The architecture of the app is documented at [docs/architecture.md](./docs/architecture.md) and for how data
flows through the app, and [docs/adr](./docs/adr) for key decisions.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…