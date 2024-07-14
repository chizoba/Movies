import shared
import SwiftUI

@main
struct iOSApp: App {
    init() {
        DependenciesKt.doInitWorld(
            dependencies: DependenciesFactory().create(
                databaseDriverFactory: IOSDatabaseDriverFactory()
            )
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

enum Inject {
    static var dependencies: Dependencies {
        DependenciesCompanion.shared.current
    }
}
