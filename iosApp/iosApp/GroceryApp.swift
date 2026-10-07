import SwiftUI
import SharedLogic

/// Two tabs, Home and Cart, with the Cart tab badge showing the total units in the Cart.
struct GroceryApp: View {
    let container: AppContainer

    private enum Tab { case home, cart }

    @State private var tab = Tab.home
    @StateObject private var store: Store<MainViewModel, MainUiState>

    init(container: AppContainer) {
        self.container = container
        _store = StateObject(wrappedValue: Store(
            container.mainViewModelOwner(),
            state: { $0.uiState },
            stateFlow: { $0.uiStateFlow }
        ))
    }

    var body: some View {
        TabView(selection: $tab) {
            HomeScreen(container: container)
                .tabItem { Label { Text(MR.strings.shared.tab_home) } icon: { Image(systemName: "house.fill") } }
                .tag(Tab.home)
            CartScreen(container: container, onStartShopping: { tab = .home })
                .tabItem { Label { Text(MR.strings.shared.tab_cart) } icon: { Image(systemName: "cart.fill") } }
                .badge(store.state.cartBadgeText.map { Text($0) })
                .tag(Tab.cart)
        }
        .tint(Palette.primary)
    }
}
