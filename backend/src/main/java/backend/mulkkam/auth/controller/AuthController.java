package backend.mulkkam.auth.controller;

import backend.mulkkam.auth.dto.KakaoSigninRequest;
import backend.mulkkam.auth.dto.OauthLoginResponse;
import backend.mulkkam.auth.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인 및 회원가입", description = "사용자 로그인 / 회원가입 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(
            summary = "카카오 로그인",
            description = "카카오 액세스 토큰으로 로그인 처리 후, 애플리케이션 토큰과 온보딩 여부를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공 응답")
    @PostMapping("/kakao")
    public OauthLoginResponse signInWithKakao(
            @RequestBody
            KakaoSigninRequest kakaoSigninRequest
    ) {
        return kakaoAuthService.signIn(kakaoSigninRequest);
    }
}
