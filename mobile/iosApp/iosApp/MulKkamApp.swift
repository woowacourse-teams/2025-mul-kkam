import SwiftUI
import Shared

@main
struct MulKkamApp: App {
    init() {
        let baseUrl = Bundle.main.object(forInfoDictionaryKey: "BASE_URL") as? String
            ?? "http://localhost:8080"
#if DEBUG
        let isDebug = true
#else
        let isDebug = false
#endif
        HelperKt.doInitKoin(baseUrl: baseUrl, isDebug: isDebug)
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
