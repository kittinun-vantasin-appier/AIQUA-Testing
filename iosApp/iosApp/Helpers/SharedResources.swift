import SwiftUI
import SharedLogic

// Strings and colors come from sharedLogic's moko-resources: the same ones Android uses.
// (`SharedLogic.` is needed because SwiftUI has its own ColorResource/StringResource types.)

extension Text {
    init(_ resource: SharedLogic.StringResource, _ args: Any...) {
        self.init(args.isEmpty ? resource.desc().localized() : resource.format(args_: args).localized())
    }
}

extension Color {
    init(_ resource: SharedLogic.ColorResource) {
        self.init(uiColor: resource.getUIColor())
    }
}
