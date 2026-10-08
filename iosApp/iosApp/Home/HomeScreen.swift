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
                .background(Color(MR.colors.shared.surface)) // same page background as Android, incl. dark mode
                .navigationTitle(Text(MR.strings.shared.home_title))
        }
    }

    // Content wins: once there are sections, keep showing them whatever else is going on.
    @ViewBuilder
    private var content: some View {
        let state = store.state
        if state.sections.isEmpty && state.isLoading {
            ProgressView().controlSize(.large).tint(Color(MR.colors.shared.primary))
        } else if state.sections.isEmpty && state.error != nil {
            VStack(spacing: 16) {
                Text(MR.strings.shared.home_error)
                Button { store.viewModel.onRetryClick() } label: { Text(MR.strings.shared.retry) }
                    .buttonStyle(.borderedProminent)
                    .buttonBorderShape(.capsule)
                    .tint(Color(MR.colors.shared.primary))
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
                            columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: 3),
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
            .foregroundStyle(Color(MR.colors.shared.primary))
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(Color(MR.colors.shared.surface_container))
    }
}

/// Everything for one Product lives inside one card: the stepper floats over the top of the emoji,
/// and the name and price sit along the bottom.
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
                    .font(.system(size: 40))
                    .padding(.top, 16)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                QuantityStepper(
                    quantity: row.quantity,
                    canAdd: row.canAdd,
                    fillWidth: row.quantity > 0,
                    compact: true,
                    onAdd: onAdd,
                    onDecrement: onDecrement,
                    onRemove: onRemove
                )
                .padding(6)
            }
            .aspectRatio(1.5, contentMode: .fit)

            VStack(alignment: .leading, spacing: 0) {
                Text(row.product.name)
                    .lineLimit(1)
                    .minimumScaleFactor(0.8)
                Text(row.priceText)
                    .fontWeight(.semibold)
                    .foregroundStyle(Color(MR.colors.shared.primary))
            }
            .font(.footnote)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 8)
            .padding(.vertical, 6)
        }
        .background(Color(MR.colors.shared.surface_container_high), in: RoundedRectangle(cornerRadius: 16))
    }
}
