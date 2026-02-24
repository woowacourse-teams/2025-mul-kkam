import Foundation
import Shared

final class LoginPlatform {
    private let kakaoHandler = KakaoLoginHandler()
    private let appleHandler = AppleLoginHandler()

    func login(
        authPlatform: AuthPlatform,
        onSuccess: @escaping (String) -> KotlinUnit,
        onError: @escaping (String) -> KotlinUnit
    ) {
        switch authPlatform {
        case .kakao:
            kakaoHandler.login(
                onSuccess: onSuccess,
                onFailure: onError
            )

        case .apple:
            appleHandler.login(
                onSuccess: onSuccess,
                onFailure: onError
            )

        default:
            break
        }
    }
}
