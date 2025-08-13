package backend.mulkkam.auth.controller;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.dto.KakaoSigninRequest;
import backend.mulkkam.auth.dto.OauthLoginResponse;
import backend.mulkkam.auth.service.KakaoAuthService;
import backend.mulkkam.common.exception.FailureBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "계정 관리", description = "사용자 계정 로그인 / 로그아웃 / 회원가입 / 회원탈퇴 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "카카오 로그인", description = "카카오 액세스 토큰으로 로그인 처리 후, 애플리케이션 토큰과 온보딩 여부를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공 응답")
    @ApiResponse(responseCode = "400", description = "잘못된 카카오 액세스 토큰", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 액세스 토큰", summary = "Kakao API 호출 실패", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @PostMapping("/kakao")
    public OauthLoginResponse signInWithKakao(
            @RequestBody
            KakaoSigninRequest kakaoSigninRequest
    ) {
        return kakaoAuthService.signIn(kakaoSigninRequest);
    }

    @Operation(summary = "로그아웃", description = "사용자를 로그아웃 상태로 기록합니다. 성공 응답 수신시 액세스 토큰과 리프레시 토큰을 삭제해주셔야 합니다.")
    @ApiResponse(responseCode = "204", description = "성공 응답")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Parameter(hidden = true) OauthAccount oauthAccount) {
        return ResponseEntity.noContent().build();
    }
}
