import SwiftUI
import SharedLogic
import KMPNativeCoroutinesAsync
import KMPNativeCoroutinesCore

final class Store<VM: AnyObject, State>: ObservableObject {
    let viewModel: VM
    @Published private(set) var state: State

    private var observation: Task<Void, Never>?
    private let clear: () -> Void

    init(
        _ owner: ViewModelOwner<VM>,
        state: (VM) -> State,
        stateFlow: (VM) -> NativeFlow<State, Error, KotlinUnit>
    ) {
        viewModel = owner.viewModel
        self.state = state(owner.viewModel)
        clear = { owner.clear() }

        let stateFlow = stateFlow(owner.viewModel)
        observation = Task { @MainActor [weak self] in
            do {
                for try await value in asyncSequence(for: stateFlow) {
                    guard let self else { return }
                    // Like Compose, state changes don't animate unless a view asks for it.
                    var transaction = Transaction()
                    transaction.disablesAnimations = true
                    withTransaction(transaction) { self.state = value }
                }
            } catch {
                // The flow only ends when the observation is cancelled.
            }
        }
    }

    deinit {
        observation?.cancel()
        clear()
    }
}
