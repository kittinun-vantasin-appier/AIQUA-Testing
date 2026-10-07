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
            .background(Color(MR.colors.shared.surface)) // same page background as Android, incl. dark mode
            .navigationTitle(Text(MR.strings.shared.cart_title))
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
                        .foregroundStyle(Color(MR.colors.shared.primary))
                    Text(MR.strings.shared.price_each, line.unitPriceText)
                        .foregroundStyle(Color(MR.colors.shared.on_surface_variant))
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
            Text(MR.strings.shared.cart_empty)
                .font(.headline)
                .padding(.top, 16)
            Button(action: onStartShopping) { Text(MR.strings.shared.start_shopping) }
                .buttonStyle(.borderedProminent)
                .buttonBorderShape(.capsule)
                .tint(Color(MR.colors.shared.primary))
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
                Text(MR.strings.shared.total).font(.headline)
                Spacer()
                Text(totalText).font(.title2.bold())
            }
            Button(action: onBuy) {
                Text(MR.strings.shared.buy)
                    .font(.headline)
                    .foregroundStyle(Color(MR.colors.shared.on_primary))
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(Color(MR.colors.shared.primary), in: Capsule())
            }
            .buttonStyle(.plain)
        }
        .padding(16)
        .background(Color(MR.colors.shared.surface_container))
    }
}

private struct OrderOverlayView: View {
    let state: CartUiState

    var body: some View {
        VStack(spacing: 0) {
            if let placedTotalText = state.placedOrderTotalText {
                Text("✅").font(.system(size: 56))
                Text(MR.strings.shared.order_placed)
                    .font(.title2)
                    .padding(.top, 12)
                Text(placedTotalText)
                    .font(.title3.bold())
                    .padding(.top, 8)
            } else if state.orderError != nil {
                Text("❌").font(.system(size: 56))
                Text(MR.strings.shared.order_failed)
                    .font(.title2)
                    .padding(.top, 12)
                Text(MR.strings.shared.order_failed_hint)
                    .font(.subheadline)
                    .foregroundStyle(Color(MR.colors.shared.on_surface_variant))
                    .multilineTextAlignment(.center)
                    .padding(.top, 8)
            } else {
                ProgressView()
                    .controlSize(.large)
                    .tint(Color(MR.colors.shared.primary))
                Text(MR.strings.shared.placing_order)
                    .font(.headline)
                    .padding(.top, 24)
            }
        }
        .padding(32)
        .frame(minWidth: 220)
        .background(Color(MR.colors.shared.surface_container_high), in: RoundedRectangle(cornerRadius: 28))
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
