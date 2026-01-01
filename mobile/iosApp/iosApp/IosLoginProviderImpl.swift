import Foundation
import Shared
import KakaoSDKUser

class IosLoginProviderImpl: NSObject, IosLoginProvider {
    func loginWithKakao(onSuccess: @escaping (String) -> Void, onFailure: @escaping (String) -> Void) {
        if UserApi.isKakaoTalkLoginAvailable() {
            loginWithKakaoTalk(onSuccess: onSuccess, onFailure: onFailure)
        } else {
            loginWithKakaoAccount(onSuccess: onSuccess, onFailure: onFailure)
        }
    }
    
    private func loginWithKakaoTalk(onSuccess: @escaping (String) -> Void, onFailure: @escaping (String) -> Void) {
        UserApi.shared.loginWithKakaoTalk { token, error in
            if let error = error {
                print(error)
                onFailure(error.localizedDescription)
            } else {
                onSuccess(token?.accessToken ?? "")
            }
        }
    }
    
    private func loginWithKakaoAccount(onSuccess: @escaping (String) -> Void, onFailure: @escaping (String) -> Void) {
        UserApi.shared.loginWithKakaoAccount { token, error in
            if let error = error {
                print(error)
                onFailure(error.localizedDescription)
            } else {
                onSuccess(token?.accessToken ?? "")
            }
        }
    }
}
