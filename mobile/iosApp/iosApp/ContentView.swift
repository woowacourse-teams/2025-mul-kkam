import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    let loginPlatform = LoginPlatform()
    let appVersion = (Bundle.main.infoDictionary?["MARKETING_VERSION"] as? String) ?? ""

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            onLogin: { authPlatform, onSuccess, onError in
                loginPlatform.login(
                    authPlatform: authPlatform,
                    onSuccess: onSuccess,
                    onError: onError,
                )
            },
            onRegisterPushNotification: { onTokenUpdated, onPermissionUpdated, onError in
                PushNotificationManager.shared.registerForPushNotifications(
                    onTokenUpdated: { token in
                        _ = onTokenUpdated(token)
                    },
                    onPermissionUpdated: { isGranted in
                        _ = onPermissionUpdated(KotlinBoolean(bool: isGranted))
                    },
                    onError: { errorMessage in
                        _ = onError(errorMessage)
                    },
                )
            },
            onRequestMainPermissions: {
                // TODO: iOS 건강 권한 요청은 추후 구현.
                PushNotificationManager.shared.requestNotificationPermission()
            },
            appVersion: appVersion,
        )
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea()
    }
}
