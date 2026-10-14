package com.github.kittinunf.aiqua_testing

import kotlinx.coroutines.flow.SharingStarted

/**
 * How every ViewModel shares its `uiState`: upstream flows run while a screen observes it and stop 5 seconds after the
 * last one leaves, so an Android configuration change doesn't restart them. On iOS, KMP-ObservableViewModel counts a
 * SwiftUI view holding the ViewModel (`@StateViewModel`) as a subscriber.
 *
 * So `uiState.value` is only kept current while something observes it: tests must collect `uiState` before reading it.
 */
internal val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(5_000)
