import KMPObservableViewModelCore
import Observation
import SharedLogic

// Lets SwiftUI observe the shared Kotlin ViewModels directly (KMP-ObservableViewModel): a screen holds one with
// `@StateViewModel` and reads its `uiState`; per the library, the ViewModel is cleared once SwiftUI releases it.
// `Observable` makes SwiftUI re-render only the views that read what changed.
extension Kmp_observableviewmodel_coreViewModel: @retroactive ViewModel { }
extension Kmp_observableviewmodel_coreViewModel: @retroactive Observable { }
