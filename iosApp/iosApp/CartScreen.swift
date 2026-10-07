import SwiftUI
import SharedLogic

struct CartScreen: View {
    @StateObject private var store: Store<CartViewModel, CartUiState>
    let onStartShopping: () -> Void

    init(container: AppContainer, onStartShopping: @escaping () -> Void) {
        _store = StateObject(wrappedValue: Store(
            container.cartViewModelOwner(),
            state: { $0.uiState },
            stateFlow: { $0.uiStateFlow }
        ))
        self.onStartShopping = onStartShopping
    }

    var body: some View {
        NavigationStack {
            Group {
                if store.state.isEmpty {
                    EmptyCart(onStartShopping: onStartShopping)
                } else {
                    lines
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .navigationTitle("Cart")
            .safeAreaInset(edge: .bottom) {
                if !store.state.isEmpty {
                    CheckoutBar(totalText: store.state.totalText) { store.viewModel.onBuyClick() }
                }
            }
        }
        // Covers the whole app, tab bar included; it fades in and out as the state says.
        .fadingCover(item: store.state.isOrderOverlayVisible ? store.state : nil) { state in
            OrderOverlayView(state: state)
        }
    }

    private var lines: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                ForEach(store.state.lines, id: \.product.id) { line in
                    CartLineItem(
                        line: line,
                        onAdd: { store.viewModel.onAddClick(product: line.product) },
                        onDecrement: { store.viewModel.onDecrementClick(product: line.product) },
                        onRemove: { store.viewModel.onRemoveClick(product: line.product) }
                    )
                }
            }
        }
    }
}

private struct CartLineItem: View {
    let line: CartLineRow
    let onAdd: () -> Void
    let onDecrement: () -> Void
    let onRemove: () -> Void

    var body: some View {
        HStack(spacing: 16) {
            ProductEmoji(emoji: line.product.emoji)
            VStack(alignment: .leading, spacing: 2) {
                Text(line.product.name)
                HStack(spacing: 6) {
                    Text(line.totalText)
                        .fontWeight(.semibold)
                        .foregroundStyle(Palette.primary)
                    Text("\(line.unitPriceText) each")
                        .foregroundStyle(Palette.onSurfaceVariant)
                }
                .font(.subheadline)
            }
            Spacer(minLength: 0)
            QuantityStepper(
                quantity: line.quantity,
                canAdd: line.canAdd,
                onAdd: onAdd,
                onDecrement: onDecrement,
                onRemove: onRemove
            )
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
    }
}

private struct EmptyCart: View {
    let onStartShopping: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            Text("🛒").font(.system(size: 64))
            Text("Your cart is empty")
                .font(.headline)
                .padding(.top, 16)
            Button("Start shopping", action: onStartShopping)
                .buttonStyle(.borderedProminent)
                .buttonBorderShape(.capsule)
                .tint(Palette.primary)
                .padding(.top, 24)
        }
    }
}

private struct CheckoutBar: View {
    let totalText: String
    let onBuy: () -> Void

    var body: some View {
        VStack(spacing: 12) {
            HStack {
                Text("Total").font(.headline)
                Spacer()
                Text(totalText).font(.title2.bold())
            }
            Button(action: onBuy) {
                Text("Buy")
                    .font(.headline)
                    .foregroundStyle(Palette.onPrimary)
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(Palette.primary, in: Capsule())
            }
            .buttonStyle(.plain)
        }
        .padding(16)
        .background(Palette.surfaceContainer)
    }
}

private struct OrderOverlayView: View {
    let state: CartUiState

    var body: some View {
        VStack(spacing: 0) {
            if let placedTotalText = state.placedOrderTotalText {
                Text("✅").font(.system(size: 56))
                Text("Order placed")
                    .font(.title2)
                    .padding(.top, 12)
                Text(placedTotalText)
                    .font(.title3.bold())
                    .padding(.top, 8)
            } else if state.orderError != nil {
                Text("❌").font(.system(size: 56))
                Text("Order failed")
                    .font(.title2)
                    .padding(.top, 12)
                Text("Your cart is still here. Please try again.")
                    .font(.subheadline)
                    .foregroundStyle(Palette.onSurfaceVariant)
                    .multilineTextAlignment(.center)
                    .padding(.top, 8)
            } else {
                ProgressView()
                    .controlSize(.large)
                    .tint(Palette.primary)
                Text("Placing your order…")
                    .font(.headline)
                    .padding(.top, 24)
            }
        }
        .padding(32)
        .frame(minWidth: 220)
        .background(Palette.surfaceContainerHigh, in: RoundedRectangle(cornerRadius: 28))
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
