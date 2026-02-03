import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    let loginPlatform = LoginPlatform()
    
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
                    onTokenUpdated: onTokenUpdated,
                    onPermissionUpdated: onPermissionUpdated,
                    onError: onError,
                )
            },
            onRequestMainPermissions: {
                // TODO: iOS Health and notification permission requests need implementation.
            },
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
