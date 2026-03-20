import SwiftUI
import Shared
import KakaoSDKCommon

@main
struct MulKkamApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        let kakaoNativeAppKey = (Bundle.main.infoDictionary?["KEY_KAKAO"] as? String) ?? ""
        KakaoSDK.initSDK(appKey: kakaoNativeAppKey)

        let baseUrl = Bundle.main.object(forInfoDictionaryKey: "BASE_URL") as? String ?? "http://localhost:8080"
#if DEBUG
        let isDebug = true
#else
        let isDebug = false
#endif
        KoinHelper().doInitKoin(
            baseUrl: baseUrl,
            isDebug: isDebug,
            firebaseBridge: FirebaseLoggingBridgeImpl()
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
