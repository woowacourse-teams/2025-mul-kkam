기본 콘텐츠로 건너뛰기
Firebase
빌드

실행

솔루션
가격 책정
더보기
검색
/


한국어
블로그
Studio
콘솔로 이동

Documentation
FCM
Firebase
Documentation
FCM
도움이 되었나요?

의견 보내기Apple 플랫폼에서 Firebase 클라우드 메시징 클라이언트 앱 설정

Apple 클라이언트 앱의 경우 Firebase Cloud Messaging APNs 인터페이스를 통해 최대 4,096바이트의 알림 및 데이터 페이로드를 수신할 수 있습니다.

Objective-C 또는 Swift로 클라이언트 코드를 작성하려면 FIRMessaging API를 사용하는 것이 좋습니다. 빠른 시작 예시에서는 두 언어의 샘플 코드를 제공합니다.

Firebase Cloud Messaging의 메서드 재구성
FCM SDK는 FCM 등록 토큰에 APN 토큰을 매핑하고 다운스트림 메시지 콜백 처리 중에 애널리틱스 데이터를 캡처하는 등 두 주요 영역에서 메서드를 재구성합니다. 재구성을 사용하지 않으려는 개발자는 앱의 Info.plist 파일에 FirebaseAppDelegateProxyEnabled 플래그를 추가하고 NO(불리언 값)로 설정하여 재구성을 사용 중지할 수 있습니다. 이 가이드의 관련 영역에서는 메서드 재구성을 사용할 때와 그렇지 않을 때의 코드 예시를 모두 제공합니다.

중요: iOS용 Firebase Unity SDK를 사용하는 경우 메서드 재구성을 사용 중지하지 마세요. SDK에는 재구성이 필요하며 재구성이 없으면 FCM 토큰 처리와 같은 주요 Firebase 기능이 제대로 작동하지 않습니다.
Apple 프로젝트에 Firebase 추가
아직 추가하지 않았다면 Apple 프로젝트에 Firebase를 추가합니다.

APN 인증 키 업로드
Firebase에 APNs 인증 키를 업로드합니다. 아직 APNs 인증 키가 없다면 Apple Developer Member Center에서 만드세요.

Firebase Console 프로젝트 내에서 톱니바퀴 아이콘을 선택하고 프로젝트 설정을 선택한 다음 클라우드 메시징 탭을 선택합니다.

iOS 앱 구성의 APN 인증 키에서 업로드를 클릭하여 개발 인증 키, 프로덕션 인증 키 또는 둘 다를 업로드합니다. 최소 하나 이상이 필요합니다.

키를 저장한 위치로 이동하여 키를 선택하고 열기를 클릭합니다. 해당하는 키 ID(Apple Developer Member Center에서 확인 가능)를 추가하고 업로드를 클릭합니다.

원격 알림 등록
애플리케이션이 시작될 때 또는 적절한 시점에 원격 알림에 앱을 등록합니다. 다음과 같이 registerForRemoteNotifications를 호출합니다.
참고: SwiftUI 앱은 UIApplicationDelegateAdaptor 또는 NSApplicationDelegateAdaptor 속성 래퍼를 사용하여 적절한 앱 대리자 프로토콜에 해당하는 유형을 제공해야 합니다.
Swift
Objective-C

UNUserNotificationCenter.current().delegate = self

let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
UNUserNotificationCenter.current().requestAuthorization(
options: authOptions,
completionHandler: { _, _ in }
)

application.registerForRemoteNotifications()
UNUserNotificationCenter의 delegate 속성 및 FIRMessaging의 delegate 속성을 할당해야 합니다. 예를 들어 iOS 앱에서는 앱 대리자의 applicationWillFinishLaunchingWithOptions: 또는 applicationDidFinishLaunchingWithOptions: 메서드에서 할당합니다.
등록 토큰 액세스
기본적으로 FCM SDK는 앱을 시작할 때 클라이언트 앱 인스턴스용 등록 토큰을 생성합니다. APNs 기기 토큰과 마찬가지로 이 토큰을 사용하여 타겟팅된 알림을 앱의 모든 특정 인스턴스로 전송할 수 있습니다.

Apple 플랫폼이 일반적으로 앱 시작 시 APNs 기기 토큰을 전달하는 것과 마찬가지로 FCM은 FIRMessagingDelegate의 messaging:didReceiveRegistrationToken: 메서드를 통해 등록 토큰을 제공합니다. FCM SDK는 최초 앱 시작 시 그리고 토큰이 업데이트되거나 무효화될 때마다 신규 또는 기존 토큰을 가져옵니다. 어떠한 경우든 FCM SDK는 유효한 토큰이 있는 messaging:didReceiveRegistrationToken:을 호출합니다.

토큰 관리에 지원 중단된 인스턴스 ID API를 계속 사용하는 앱은 여기에 설명된 FCM API를 사용하도록 모든 토큰 로직을 업데이트해야 합니다.
다음과 같은 경우에 등록 토큰이 변경될 수 있습니다.

새 기기에서 앱 복원
사용자가 앱 제거/재설치
사용자가 앱 데이터 삭제
메시지 대리자 설정
등록 토큰을 수신하려면 [FIRApp configure]를 호출한 후 메시지 대리자 프로토콜을 구현하고 FIRMessaging의 delegate 속성을 설정합니다. 예를 들어 애플리케이션 대리자가 메시지 대리자 프로토콜을 준수하는 경우 application:didFinishLaunchingWithOptions:에서 대리자를 애플리케이션 대리자로 설정할 수 있습니다.

Swift
Objective-C

Messaging.messaging().delegate = self
현재 등록 토큰 가져오기
등록 토큰은 messaging:didReceiveRegistrationToken: 메서드를 통해 전달됩니다. 일반적으로 앱 시작 시 등록 토큰을 사용하여 이 메서드를 한 번 호출합니다. 이 메서드가 호출되면 다음과 같은 작업을 할 수 있습니다.

새 등록 토큰이라면 애플리케이션 서버에 전송합니다.
등록 토큰을 주제에 구독 처리합니다. 이 작업은 신규 구독 또는 사용자의 앱 재설치 같은 상황에서만 필요합니다.
token(completion:)을 사용하여 직접 토큰을 가져올 수 있습니다. 어떤 방식으로든 토큰 가져오기에 실패할 경우 null이 아닌 오류가 제공됩니다.

Swift
Objective-C

Messaging.messaging().token { token, error in
if let error = error {
print("Error fetching FCM registration token: \(error)")
} else if let token = token {
print("FCM registration token: \(token)")
self.fcmRegTokenMessage.text  = "Remote FCM registration token: \(token)"
}
}
언제든지 이 메서드를 사용하여 토큰을 저장하지 않고도 토큰에 액세스할 수 있습니다.

토큰 갱신 모니터링
토큰이 업데이트될 때마다 알림을 받으려면 메시지 대리자 프로토콜을 준수하는 대리자를 제공합니다. 다음은 대리자를 등록하고 적절한 대리자 메서드를 추가하는 예시입니다.

Swift
Objective-C

func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
print("Firebase registration token: \(String(describing: fcmToken))")

let dataDict: [String: String] = ["token": fcmToken ?? ""]
NotificationCenter.default.post(
name: Notification.Name("FCMToken"),
object: nil,
userInfo: dataDict
)
// TODO: If necessary send token to application server.
// Note: This callback is fired at each app startup and whenever a new token is generated.
}
대리자 메서드를 제공하는 대신 kFIRMessagingRegistrationTokenRefreshNotification이라는 NSNotification을 수신 대기할 수도 있습니다. 토큰 속성은 항상 현재 토큰 값을 갖습니다.

재구성 사용 중지됨: APNs 토큰과 등록 토큰 매핑
메서드 재구성을 사용 중지했거나 SwiftUI 앱을 빌드 중인 경우 APN 토큰을 명시적으로 FCM 등록 토큰에 매핑해야 합니다. application(_:didRegisterForRemoteNotificationsWithDeviceToken:) 메서드를 구현하여 APNs 토큰을 가져와 Messaging의 apnsToken 속성을 설정합니다.

Swift
Objective-C

func application(application: UIApplication,
didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
Messaging.messaging().apnsToken = deviceToken
}
FCM 등록 토큰이 생성된 후, 재구성이 사용 설정되었을 때와 동일한 메서드를 사용하여 토큰에 액세스하고 새로고침 이벤트를 수신 대기할 수 있습니다.

자동 초기화 방지
FCM 등록 토큰이 생성되면 라이브러리는 식별자와 구성 데이터를 Firebase에 업로드합니다. 사용자에게 먼저 명시적인 수신 동의를 얻으려면 FCM을 사용 중지하여 구성 시 토큰이 생성되지 않게 하면 됩니다. 이렇게 하려면 메타데이터 값을 Info.plist(GoogleService-Info.plist 아님)에 추가합니다.

FirebaseMessagingAutoInitEnabled = NO

FCM을 다시 사용 설정하려면 런타임 호출을 만들면 됩니다.

Swift
Objective-C

Messaging.messaging().autoInitEnabled = true
이 값을 설정하면 앱을 다시 시작해도 값이 유지됩니다.

다음 단계
Apple 클라이언트를 설정한 후에는 메시지 처리 및 기타 고급 동작을 앱에 추가할 수 있습니다. 자세한 내용은 다음 가이드를 참조하세요.

Apple 앱에서 메시지 수신
주제 메시지 전송
기기 그룹으로 전송
도움이 되었나요?

의견 보내기
달리 명시되지 않는 한 이 페이지의 콘텐츠에는 Creative Commons Attribution 4.0 라이선스에 따라 라이선스가 부여되며, 코드 샘플에는 Apache 2.0 라이선스에 따라 라이선스가 부여됩니다. 자세한 내용은 Google Developers 사이트 정책을 참조하세요. 자바는 Oracle 및/또는 Oracle 계열사의 등록 상표입니다.

최종 업데이트: 2025-10-06(UTC)
