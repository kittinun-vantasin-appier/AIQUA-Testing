# Screen ViewModels live in sharedLogic

Each screen's ViewModel and its UI state live in `sharedLogic`, not in the platform apps. Compose and SwiftUI only render that state and pass user actions back. This guarantees both platforms behave the same, and leaves one place to hook AIQUA tracking later. iOS observes the ViewModels' UI state as Swift `AsyncSequence`s through [KMP-NativeCoroutines](https://github.com/rickclephas/KMP-NativeCoroutines).
