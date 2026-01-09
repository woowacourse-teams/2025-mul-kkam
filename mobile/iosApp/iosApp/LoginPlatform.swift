import Foundation
import Shared
import KakaoSDKUser

class LoginPlatform: NSObject {
    func login(authPlatform: AuthPlatform, onSuccess: @escaping (String) -> KotlinUnit, onError: @escaping (String) -> KotlinUnit) {
        switch authPlatform {
        case AuthPlatform.kakao:
            loginWithKakao(
                onSuccess: onSuccess,
                onFailure: onError
            )
            
        case AuthPlatform.apple:
            // TODO: 애플 로그인 구현
            break
            
        default:
            break
        }
    }
    
    func loginWithKakao(onSuccess: @escaping (String) -> KotlinUnit, onFailure: @escaping (String) -> KotlinUnit) {
        if UserApi.isKakaoTalkLoginAvailable() {
            loginWithKakaoTalk(onSuccess: onSuccess, onFailure: onFailure)
        } else {
            loginWithKakaoAccount(onSuccess: onSuccess, onFailure: onFailure)
        }
    }
    
    private func loginWithKakaoTalk(onSuccess: @escaping (String) -> KotlinUnit, onFailure: @escaping (String) -> KotlinUnit) {
        UserApi.shared.loginWithKakaoTalk { token, error in
            if let error = error {
                print(error)
                _ = onFailure(error.localizedDescription)
            } else if let accessToken = token?.accessToken, !accessToken.isEmpty {
                _ = onSuccess(accessToken)
            } else {
                _ = onFailure("토큰을 가져올 수 없습니다")
            }
        }
    }
    
    private func loginWithKakaoAccount(onSuccess: @escaping (String) -> KotlinUnit, onFailure: @escaping (String) -> KotlinUnit) {
        UserApi.shared.loginWithKakaoAccount { token, error in
            if let error = error {
                print(error)
                _ = onFailure(error.localizedDescription)
            } else if let accessToken = token?.accessToken, !accessToken.isEmpty {
                _ = onSuccess(accessToken)
            } else {
                _ = onFailure("토큰을 가져올 수 없습니다")
            }
        }
    }
}
