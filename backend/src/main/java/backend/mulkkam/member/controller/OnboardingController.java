package backend.mulkkam.member.controller;

import backend.mulkkam.common.dto.OauthAccountDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.OnboardingStatusResponse;
import backend.mulkkam.member.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/onboarding")
@RestController
public class OnboardingController {

    private final OnboardingService onboardingService;

    @Operation(summary = "온보딩 정보 생성", description = "OAuth 인증이 완료된 회원의 온보딩 정보를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "온보딩 정보 생성 성공")
    @ApiResponse(responseCode = "400", description = "이미 온보딩된 계정", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "이미 온보딩", summary = "OauthAccount에 Member 이미 연결", value = "{\"code\":\"MEMBER_ALREADY_EXIST_IN_OAUTH_ACCOUNT\"}")
    }))
    @PostMapping
    public ResponseEntity<Void> create(
            @Parameter(hidden = true)
            OauthAccountDetails accountDetails,
            @Valid @RequestBody CreateMemberRequest createMemberRequest
    ) {
        onboardingService.create(accountDetails, createMemberRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "온보딩 상태 확인", description = "회원의 온보딩 완료 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = OnboardingStatusResponse.class)))
    @GetMapping("/check/onboarding")
    public ResponseEntity<OnboardingStatusResponse> checkOnboardingStatus(
            @Parameter(hidden = true)
            OauthAccountDetails accountDetails
    ) {
        OnboardingStatusResponse onboardingStatusResponse = onboardingService.checkOnboardingStatus(accountDetails);
        return ResponseEntity.ok(onboardingStatusResponse);
    }
}
