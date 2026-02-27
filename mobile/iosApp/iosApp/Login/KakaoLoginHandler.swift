import Foundation
import KakaoSDKUser
import KakaoSDKAuth
import Shared

final class KakaoLoginHandler {
    typealias LoginAction = (@escaping (OAuthToken?, Error?) -> Void) -> Void

    func login(
        onSuccess: @escaping (String) -> KotlinUnit,
        onFailure: @escaping (String) -> KotlinUnit
    ) {
        let loginAction: LoginAction = UserApi.isKakaoTalkLoginAvailable()
            ? { completion in
                UserApi.shared.loginWithKakaoTalk(completion: completion)
            }
            : { completion in
                UserApi.shared.loginWithKakaoAccount(completion: completion)
            }

        loginAction { token, error in
            if let error {
                _ = onFailure(error.localizedDescription)
                return
            }

            guard let accessToken = token?.accessToken, !accessToken.isEmpty else {
                _ = onFailure("토큰을 가져올 수 없습니다")
                return
            }

            _ = onSuccess(accessToken)
        }
    }
}
