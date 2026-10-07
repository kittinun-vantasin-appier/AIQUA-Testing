import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    /// One per process, so the in-memory Cart lives as long as the app does.
    private let container = AppContainer()

    var body: some Scene {
        WindowGroup {
            GroceryApp(container: container)
        }
    }
}
