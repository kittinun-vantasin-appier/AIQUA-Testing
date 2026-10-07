import SwiftUI
import SharedLogic

// Strings and colors come from sharedLogic's moko-resources: the same ones Android uses.
// (`SharedLogic.` is needed because SwiftUI has its own ColorResource/StringResource types.)

extension Text {
    /// `Text(MR.strings.shared.home_title)`, or with format arguments: `Text(MR.strings.shared.price_each, "¥98")`.
    init(_ resource: SharedLogic.StringResource, _ args: Any...) {
        self.init(args.isEmpty ? resource.desc().localized() : resource.format(args_: args).localized())
    }
}

extension Color {
    /// `Color(MR.colors.shared.primary)`, following light and dark mode.
    init(_ resource: SharedLogic.ColorResource) {
        self.init(uiColor: resource.getUIColor())
    }
}

/// Short names for the shared palette colors the screens use.
enum Palette {
    static let primary = Color(MR.colors.shared.primary)
    static let onPrimary = Color(MR.colors.shared.on_primary)
    static let primaryContainer = Color(MR.colors.shared.primary_container)
    static let onPrimaryContainer = Color(MR.colors.shared.on_primary_container)
    static let error = Color(MR.colors.shared.error)
    static let surfaceContainer = Color(MR.colors.shared.surface_container)
    static let surfaceContainerHigh = Color(MR.colors.shared.surface_container_high)
    static let onSurfaceVariant = Color(MR.colors.shared.on_surface_variant)
}
