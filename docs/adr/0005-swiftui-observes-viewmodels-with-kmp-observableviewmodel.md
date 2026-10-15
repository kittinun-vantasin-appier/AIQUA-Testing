# SwiftUI observes the shared ViewModels with KMP-ObservableViewModel

The shared ViewModels extend `ViewModel` from [KMP-ObservableViewModel](https://github.com/rickclephas/KMP-ObservableViewModel)
instead of AndroidX's, and build their `uiState` with its `stateIn`. On Android that is still an AndroidX ViewModel,
so Compose is unchanged. On iOS, a screen holds its ViewModel with `@StateViewModel` and reads `uiState` directly;
per the library's documentation, it also clears the ViewModel once SwiftUI releases it (we haven't traced this
ourselves). This replaces our own plumbing, a Swift `Store` that looped over `asyncSequence(for: uiStateFlow)` plus a
Kotlin `ViewModelOwner` to clear ViewModels, so iOS screens hold only UI code. Some Swift is unavoidable, because
SwiftUI's `ObservableObject` and `Observable` are Swift-only protocols that Kotlin can't conform to: one file,
`Helpers/KMPObservableViewModel.swift`, adds those conformances.

The cost is a dependency on both sides that must stay in step: `kmp-observableviewmodel` in `libs.versions.toml` and the
KMP-ObservableViewModel Swift package in Xcode are pinned to the same version, which must also match the Kotlin version
(1.1.0 is built for Kotlin 2.4.20). Inside a ViewModel, `viewModelScope` is the library's wrapper, so coroutines start
with `viewModelScope.coroutineScope.launch`.
