import SwiftUI
import SharedLogic

/// The inbox as a sheet over Home. Each opening creates it afresh, so each opening is a view.
struct InboxSheet: View {
    @StateObject private var store: Store<InboxViewModel, InboxUiState>

    init(container: AppContainer) {
        _store = StateObject(wrappedValue: Store(
            container.inboxViewModelOwner(),
            state: { $0.uiState },
            stateFlow: { $0.uiStateFlow }
        ))
    }

    var body: some View {
        NavigationStack {
            content
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color(MR.colors.shared.surface)) // same page background as Android, incl. dark mode
                .navigationTitle(Text(MR.strings.shared.inbox_title))
                .navigationBarTitleDisplayMode(.inline)
        }
        .onAppear { store.viewModel.onScreenViewed() }
    }

    @ViewBuilder
    private var content: some View {
        if store.state.isEmpty {
            Text(MR.strings.shared.inbox_empty)
                .foregroundStyle(Color(MR.colors.shared.on_surface_variant))
        } else {
            // Space, not separators, separates messages: 12pt inside each row plus 8pt between rows (as on Android).
            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(store.state.messages, id: \.id) { message in
                        Button { store.viewModel.onMessageClick(message: message) } label: {
                            MessageRow(message: message)
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
        }
    }
}

/// Like a notification: unread messages have a bold title and a dot at the end, centred on the whole message; read
/// ones are dimmed.
private struct MessageRow: View {
    let message: InboxMessage

    var body: some View {
        HStack(spacing: 16) {
            VStack(alignment: .leading, spacing: 4) {
                Text(message.title)
                    .fontWeight(.semibold)
                    .foregroundStyle(Color(MR.colors.shared.on_surface).opacity(message.isRead ? 0.7 : 1))
                Text(message.text)
                    .font(.subheadline)
                    .foregroundStyle(Color(MR.colors.shared.on_surface_variant))
                    .lineLimit(3)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            // Still there, but invisible, once read, so the text doesn't widen when the message is tapped.
            Circle()
                .fill(message.isRead ? Color.clear : Color(MR.colors.shared.primary))
                .frame(width: 8, height: 8)
        }
        .padding(.horizontal, 24)
        .padding(.vertical, 12)
        .contentShape(Rectangle())
    }
}
