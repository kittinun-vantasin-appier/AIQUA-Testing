This is a Kotlin Multiplatform project targeting Android, iOS.
A grocery shop (Home catalog + Cart with Buy) used as a host app for testing the AIQUA SDK.
See [CONTEXT.md](./CONTEXT.md) for the domain language, [docs/architecture.md](./docs/architecture.md) for how data
flows through the app, and [docs/adr](./docs/adr) for key decisions.

* [/androidApp](./androidApp/src/main/kotlin) is the Android app: Jetpack Compose UI with Navigation 3.

* [/iosApp](./iosApp/iosApp) is the iOS app: SwiftUI with the same screens, observing the shared ViewModels through
  the KMPNativeCoroutinesAsync Swift package. A build phase copies the shared moko-resources bundle into the app.

* [/sharedLogic](./sharedLogic/src) holds everything shared by both apps: Services, Repositories
  and the screen ViewModels (exposed to Swift through KMP-NativeCoroutines).
  Shared text, colours and the catalog ([product.json](./sharedLogic/src/commonMain/moko-resources/files/product.json))
  live in [moko-resources](./sharedLogic/src/commonMain/moko-resources).

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…