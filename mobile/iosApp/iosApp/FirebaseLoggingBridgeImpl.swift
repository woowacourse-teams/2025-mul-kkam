import FirebaseAnalytics
import FirebaseCrashlytics
import Shared

class FirebaseLoggingBridgeImpl: FirebaseLoggingBridge {
    func log(eventName: String, level: String, message: String, userId: String?) {
        var params: [String: Any] = [
            "level": level,
            "message": message,
        ]
        if let userId { params["user_id"] = userId }
        Analytics.logEvent(eventName, parameters: params)
    }

    func recordException(message: String) {
        Crashlytics.crashlytics().record(error: NSError(
            domain: "com.mulkkam",
            code: -1,
            userInfo: [NSLocalizedDescriptionKey: message]
        ))
    }
}
