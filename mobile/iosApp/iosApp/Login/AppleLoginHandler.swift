import Foundation
import AuthenticationServices
import Shared
import UIKit

final class AppleLoginHandler: NSObject {
    private var onSuccess: ((String) -> KotlinUnit)?
    private var onFailure: ((String) -> KotlinUnit)?

    func login(
        onSuccess: @escaping (String) -> KotlinUnit,
        onFailure: @escaping (String) -> KotlinUnit
    ) {
        self.onSuccess = onSuccess
        self.onFailure = onFailure

        let request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = [.fullName, .email]

        let controller = ASAuthorizationController(authorizationRequests: [request])
        controller.delegate = self
        controller.presentationContextProvider = self
        controller.performRequests()
    }
}

extension AppleLoginHandler: ASAuthorizationControllerDelegate {
    func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization authorization: ASAuthorization
    ) {
        guard
            let credential = authorization.credential as? ASAuthorizationAppleIDCredential,
            let authorizationCode = credential.authorizationCode,
            let autorizationCodeStr = String(data: authorizationCode, encoding: .utf8)
        else {
            _ = onFailure?("애플 로그인 토큰을 가져올 수 없습니다")
            return
        }

        _ = onSuccess?(autorizationCodeStr)
    }

    func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError error: Error
    ) {
        _ = onFailure?(error.localizedDescription)
    }
}

extension AppleLoginHandler: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .first { $0.activationState == .foregroundActive }?
            .windows
            .first { $0.isKeyWindow }
        ?? UIWindow()
    }
}
