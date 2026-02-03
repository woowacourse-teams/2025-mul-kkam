import FirebaseCore
import FirebaseMessaging
import UIKit
import UserNotifications

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        return true
    }

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        let sceneConfig = UISceneConfiguration(name: nil, sessionRole: connectingSceneSession.role)
        sceneConfig.delegateClass = SceneDelegate.self
        return sceneConfig
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken else { return }
        PushNotificationManager.shared.updateToken(token)
    }
}

final class PushNotificationManager {
    static let shared = PushNotificationManager()

    private var tokenUpdatedHandler: ((String) -> Void)?
    private var permissionUpdatedHandler: ((Bool) -> Void)?
    private var errorHandler: ((String) -> Void)?
    private var latestToken: String?

    private init() {}

    func registerForPushNotifications(
        onTokenUpdated: @escaping (String) -> Void,
        onPermissionUpdated: @escaping (Bool) -> Void,
        onError: @escaping (String) -> Void
    ) {
        tokenUpdatedHandler = onTokenUpdated
        permissionUpdatedHandler = onPermissionUpdated
        errorHandler = onError

        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { [weak self] isGranted, error in
            DispatchQueue.main.async {
                if let error = error {
                    self?.errorHandler?(error.localizedDescription)
                }
                self?.permissionUpdatedHandler?(isGranted)
                UIApplication.shared.registerForRemoteNotifications()
            }
        }

        if let latestToken = latestToken {
            tokenUpdatedHandler?(latestToken)
        }

        Messaging.messaging().token { [weak self] token, error in
            if let error = error {
                self?.errorHandler?(error.localizedDescription)
                return
            }
            if let token = token {
                self?.updateToken(token)
            }
        }
    }

    func updateToken(_ token: String) {
        latestToken = token
        tokenUpdatedHandler?(token)
    }
}
