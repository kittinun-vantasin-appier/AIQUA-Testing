import SwiftUI

/// The same colors as Android's `GroceryTheme`, each with a light and a dark variant.
enum Palette {
    static let primary = dynamic(light: 0x3D863E, dark: 0x7EDB7B)
    static let onPrimary = dynamic(light: 0xFFFFFF, dark: 0x1E361D)
    static let primaryContainer = dynamic(light: 0xC2EEBC, dark: 0x336A33)
    static let onPrimaryContainer = dynamic(light: 0x1E361D, dark: 0xC2EEBC)
    static let error = dynamic(light: 0xC05648, dark: 0xFFB4A5)
    static let surfaceContainer = dynamic(light: 0xF1F1F1, dark: 0x282828)
    static let surfaceContainerHigh = dynamic(light: 0xEAEAEA, dark: 0x303030)
    static let onSurfaceVariant = dynamic(light: 0x5E5E5E, dark: 0xC6C6C6)

    private static func dynamic(light: UInt32, dark: UInt32) -> Color {
        Color(UIColor { $0.userInterfaceStyle == .dark ? UIColor(rgb: dark) : UIColor(rgb: light) })
    }
}

private extension UIColor {
    convenience init(rgb: UInt32) {
        self.init(
            red: CGFloat((rgb >> 16) & 0xFF) / 255,
            green: CGFloat((rgb >> 8) & 0xFF) / 255,
            blue: CGFloat(rgb & 0xFF) / 255,
            alpha: 1
        )
    }
}
