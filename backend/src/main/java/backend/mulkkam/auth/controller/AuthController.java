package backend.mulkkam.auth.controller;

import backend.mulkkam.auth.dto.KakaoSigninRequest;
import backend.mulkkam.auth.dto.OauthLoginResponse;
import backend.mulkkam.auth.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/kakao")
    public OauthLoginResponse signInWithKakao(@RequestBody KakaoSigninRequest kakaoSigninRequest) {
        return kakaoAuthService.signIn(kakaoSigninRequest);
    }
}
