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
