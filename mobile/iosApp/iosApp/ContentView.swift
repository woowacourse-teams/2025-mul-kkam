import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    let loginPlatform = LoginPlatform()
    
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            onLogin: { authPlatform, onSuccess, onError in
                loginPlatform.login(
                    authPlatform: authPlatform,
                    onSuccess: onSuccess,
                    onError: onError,
                )
            }
        )
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea()
    }
}
