import SwiftUI
import SharedLogic

struct HomeScreen: View {
    @StateObject private var store: Store<HomeViewModel, HomeUiState>

    init(container: AppContainer) {
        _store = StateObject(wrappedValue: Store(
            container.homeViewModelOwner(),
            state: { $0.uiState },
            stateFlow: { $0.uiStateFlow }
        ))
    }

    var body: some View {
        NavigationStack {
            content
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .navigationTitle(Text(MR.strings.shared.home_title))
        }
    }

    // Content wins: once there are sections, keep showing them whatever else is going on.
    @ViewBuilder
    private var content: some View {
        let state = store.state
        if state.sections.isEmpty && state.isLoading {
            ProgressView().controlSize(.large).tint(Palette.primary)
        } else if state.sections.isEmpty && state.error != nil {
            VStack(spacing: 16) {
                Text(MR.strings.shared.home_error)
                Button { store.viewModel.onRetryClick() } label: { Text(MR.strings.shared.retry) }
                    .buttonStyle(.borderedProminent)
                    .buttonBorderShape(.capsule)
                    .tint(Palette.primary)
            }
        } else {
            catalog(state.sections)
        }
    }

    private func catalog(_ sections: [HomeSection]) -> some View {
        ScrollView {
            LazyVStack(spacing: 0, pinnedViews: [.sectionHeaders]) {
                ForEach(sections, id: \.title) { section in
                    Section {
                        LazyVGrid(
                            columns: [GridItem(.flexible(), spacing: 12), GridItem(.flexible(), spacing: 12)],
                            spacing: 16
                        ) {
                            ForEach(section.rows, id: \.product.id) { row in
                                ProductCard(
                                    row: row,
                                    onAdd: { store.viewModel.onAddClick(product: row.product) },
                                    onDecrement: { store.viewModel.onDecrementClick(product: row.product) },
                                    onRemove: { store.viewModel.onRemoveClick(product: row.product) }
                                )
                            }
                        }
                        .padding(16)
                    } header: {
                        SectionHeader(title: section.title)
                    }
                }
            }
        }
    }
}

private struct SectionHeader: View {
    let title: String

    var body: some View {
        Text(title)
            .font(.subheadline.weight(.semibold))
            .foregroundStyle(Palette.primary)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(Palette.surfaceContainer)
    }
}

/// Everything for one Product lives inside one card: the stepper floats over the top of the emoji,
/// and the name and price sit in one row along the bottom.
private struct ProductCard: View {
    let row: ProductRow
    let onAdd: () -> Void
    let onDecrement: () -> Void
    let onRemove: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            // "+" sits in the corner; once in the Cart the stepper spans the top edge.
            ZStack(alignment: row.quantity == 0 ? .topTrailing : .top) {
                Text(row.product.emoji)
                    .font(.system(size: 64))
                    .padding(.top, 24)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                QuantityStepper(
                    quantity: row.quantity,
                    canAdd: row.canAdd,
                    fillWidth: row.quantity > 0,
                    onAdd: onAdd,
                    onDecrement: onDecrement,
                    onRemove: onRemove
                )
                .padding(8)
            }
            .aspectRatio(1, contentMode: .fit)

            HStack(spacing: 8) {
                Text(row.product.name)
                    .lineLimit(1)
                    .minimumScaleFactor(0.8)
                Spacer(minLength: 0)
                Text(row.priceText)
                    .fontWeight(.semibold)
                    .foregroundStyle(Palette.primary)
            }
            .font(.subheadline)
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
        }
        .background(Palette.surfaceContainerHigh, in: RoundedRectangle(cornerRadius: 16))
    }
}
