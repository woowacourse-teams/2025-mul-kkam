import FirebaseCore
import FirebaseMessaging
import UIKit
import UserNotifications

final class PushNotificationManager {
    static let shared = PushNotificationManager()

    private var tokenUpdatedHandler: ((String) -> Void)?
    private var permissionUpdatedHandler: ((Bool) -> Void)?
    private var errorHandler: ((String) -> Void)?
    private var latestToken: String?

    private init() {
    }

    func registerForPushNotifications(
        onTokenUpdated: @escaping (String) -> Void,
        onPermissionUpdated: @escaping (Bool) -> Void,
        onError: @escaping (String) -> Void
    ) {
        tokenUpdatedHandler = onTokenUpdated
        permissionUpdatedHandler = onPermissionUpdated
        errorHandler = onError

        updateAuthorizationStatus()

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

    func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { [weak self] isGranted, error in
            DispatchQueue.main.async {
                if let error = error {
                    self?.errorHandler?(error.localizedDescription)
                }
                self?.permissionUpdatedHandler?(isGranted)
                if isGranted {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            }
        }
    }

    func updateToken(_ token: String) {
        latestToken = token
        tokenUpdatedHandler?(token)
    }

    private func updateAuthorizationStatus() {
        UNUserNotificationCenter.current().getNotificationSettings { [weak self] settings in
            let isGranted = settings.authorizationStatus == .authorized
                || settings.authorizationStatus == .provisional
                || settings.authorizationStatus == .ephemeral
            DispatchQueue.main.async {
                self?.permissionUpdatedHandler?(isGranted)
            }
        }
    }
}
