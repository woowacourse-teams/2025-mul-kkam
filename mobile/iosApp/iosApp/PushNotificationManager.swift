import FirebaseCore
import UIKit
import UserNotifications

final class PushNotificationManager {
    static let shared = PushNotificationManager()

    private var tokenUpdatedHandler: ((String) -> Void)?
    private var permissionUpdatedHandler: ((Bool) -> Void)?
    private var errorHandler: ((String) -> Void)?
    private var latestToken: String?
    private var hasRegisteredDidBecomeActiveObserver = false

    func registerForPushNotifications(
        onTokenUpdated: @escaping (String) -> Void,
        onPermissionUpdated: @escaping (Bool) -> Void,
        onError: @escaping (String) -> Void
    ) {
        tokenUpdatedHandler = onTokenUpdated
        permissionUpdatedHandler = onPermissionUpdated
        errorHandler = onError

        updateAuthorizationStatus()
        registerDidBecomeActiveObserverIfNeeded()

        if let latestToken = latestToken {
            tokenUpdatedHandler?(latestToken)
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
        if latestToken == token {
            return
        }
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

    private func registerDidBecomeActiveObserverIfNeeded() {
        if hasRegisteredDidBecomeActiveObserver {
            return
        }
        hasRegisteredDidBecomeActiveObserver = true
        NotificationCenter.default.addObserver(
            forName: UIApplication.didBecomeActiveNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.updateAuthorizationStatus()
        }
    }
}
