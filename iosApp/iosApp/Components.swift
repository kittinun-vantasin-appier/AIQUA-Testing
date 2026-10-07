import SwiftUI

/// "+" when the Product isn't in the Cart, otherwise `− n +`.
/// At 1 the − becomes ✕, which removes the Cart Line.
/// With `fillWidth` the buttons spread to the edges, otherwise it hugs its content.
struct QuantityStepper: View {
    let quantity: Int32
    let canAdd: Bool
    var fillWidth = false
    let onAdd: () -> Void
    let onDecrement: () -> Void
    let onRemove: () -> Void

    var body: some View {
        if quantity == 0 {
            Button(action: onAdd) {
                Image(systemName: "plus")
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundStyle(Palette.onPrimary)
                    .frame(width: 40, height: 40)
                    .background(Palette.primary, in: Circle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Add to cart")
        } else {
            HStack(spacing: 0) {
                if quantity == 1 {
                    stepButton("xmark", tint: Palette.error, label: "Remove from cart", action: onRemove)
                } else {
                    stepButton("minus", label: "Decrease quantity", action: onDecrement)
                }
                if fillWidth { Spacer(minLength: 0) }
                Text("\(quantity)")
                    .font(.headline)
                    .foregroundStyle(Palette.onPrimaryContainer)
                    .frame(minWidth: 16)
                if fillWidth { Spacer(minLength: 0) }
                stepButton("plus", label: "Increase quantity", action: onAdd)
                    .disabled(!canAdd)
                    .opacity(canAdd ? 1 : 0.38)
            }
            .frame(maxWidth: fillWidth ? .infinity : nil)
            .frame(height: 40)
            .background(Palette.primaryContainer, in: Capsule())
        }
    }

    private func stepButton(
        _ systemName: String,
        tint: Color = Palette.onPrimaryContainer,
        label: String,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            Image(systemName: systemName)
                .font(.system(size: 16, weight: .semibold))
                .foregroundStyle(tint)
                .frame(width: 44, height: 40)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
    }
}

struct ProductEmoji: View {
    let emoji: String

    var body: some View {
        Text(emoji)
            .font(.system(size: 26))
            .frame(width: 48, height: 48)
            .background(Palette.surfaceContainerHigh, in: RoundedRectangle(cornerRadius: 12))
    }
}

extension View {
    /// A full-screen cover that fades in and out (instead of sliding up), dims everything behind it, tab bar included,
    /// and can't be swiped away. It shows while `item` is non-nil, and keeps showing the last item while it fades out.
    func fadingCover<Item: Equatable, Cover: View>(
        item: Item?,
        @ViewBuilder content: @escaping (Item) -> Cover
    ) -> some View {
        modifier(FadingCover(item: item, cover: content))
    }
}

private struct FadingCover<Item: Equatable, Cover: View>: ViewModifier {
    let item: Item?
    let cover: (Item) -> Cover

    @State private var shownItem: Item?
    @State private var isPresented = false
    @State private var isVisible = false

    func body(content: Content) -> some View {
        content
            .fullScreenCover(isPresented: $isPresented) {
                ZStack {
                    Color.black.opacity(0.4).ignoresSafeArea()
                    if let shownItem { cover(shownItem) }
                }
                .opacity(isVisible ? 1 : 0)
                .presentationBackground(.clear)
                .interactiveDismissDisabled()
                .onAppear { withAnimation(.easeOut(duration: 0.2)) { isVisible = true } }
            }
            .onChange(of: item, initial: true) { _, newItem in
                if let newItem {
                    shownItem = newItem
                    if !isPresented { withoutAnimation { isPresented = true } }
                } else if isPresented {
                    withAnimation(.easeIn(duration: 0.2)) {
                        isVisible = false
                    } completion: {
                        withoutAnimation { isPresented = false }
                    }
                }
            }
    }

    private func withoutAnimation(_ change: () -> Void) {
        var transaction = Transaction()
        transaction.disablesAnimations = true
        withTransaction(transaction, change)
    }
}
