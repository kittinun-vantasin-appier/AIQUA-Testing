# Screen ViewModels live in sharedLogic

Each screen's ViewModel and its UI state live in `sharedLogic`, not in the platform apps. Compose and SwiftUI only render that state and pass user actions back. This guarantees both platforms behave the same, and leaves one place to hook AIQUA tracking later. SwiftUI observes them through KMP-ObservableViewModel ([ADR-0005](./0005-swiftui-observes-viewmodels-with-kmp-observableviewmodel.md)).
