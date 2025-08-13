package backend.mulkkam.member.controller;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.OnboardingStatusResponse;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.ModifyIsNightNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.dto.response.ProgressInfoResponse;
import backend.mulkkam.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원", description = "회원 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 정보 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping
    public ResponseEntity<MemberResponse> get(
            @Parameter(hidden = true)
            Member member
    ) {
        MemberResponse memberResponse = memberService.get(member);
        return ResponseEntity.ok(memberResponse);
    }

    @Operation(summary = "신체 정보 수정", description = "회원의 신체 정보(성별, 체중)를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 신체 정보", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "잘못된 신체 정보", summary = "형식/범위 오류", value = "{\"code\":\"INVALID_METHOD_ARGUMENT\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PostMapping("/physical-attributes")
    public ResponseEntity<Void> modifyPhysicalAttributes(
            @Parameter(hidden = true)
            Member member,
            @RequestBody PhysicalAttributesModifyRequest physicalAttributesModifyRequest
    ) {
        memberService.modifyPhysicalAttributes(
                physicalAttributesModifyRequest,
                member
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "닉네임 중복 검사", description = "사용하려는 닉네임의 중복 여부를 검사합니다.")
    @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임")
    @ApiResponse(responseCode = "400", description = "이전과 동일한 닉네임", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "이전과 동일", summary = "닉네임 미변경", value = "{\"code\":\"SAME_AS_BEFORE_NICKNAME\"}")
    }))
    @ApiResponse(responseCode = "400", description = "잘못된 닉네임 형식", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "형식 오류", summary = "길이 제약 위반", value = "{\"code\":\"INVALID_MEMBER_NICKNAME\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "409", description = "중복된 닉네임", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "중복 닉네임", summary = "이미 존재", value = "{\"code\":\"DUPLICATE_MEMBER_NICKNAME\"}")
    }))
    @GetMapping("/nickname/validation")
    public ResponseEntity<Void> checkForDuplicates(
            @Parameter(hidden = true)
            Member member,
            @Parameter(description = "검사할 닉네임", required = true, example = "밍곰")
            @RequestParam String nickname
    ) {
        memberService.validateDuplicateNickname(
                nickname,
                member
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "닉네임 수정", description = "회원의 닉네임을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 닉네임 형식", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "형식 오류", summary = "길이 제약 위반", value = "{\"code\":\"INVALID_MEMBER_NICKNAME\"}")
    }))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "409", description = "중복된 닉네임", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "중복 닉네임", summary = "이미 존재", value = "{\"code\":\"DUPLICATE_MEMBER_NICKNAME\"}")
    }))
    @PatchMapping("/nickname")
    public ResponseEntity<Void> modifyNickname(
            @Parameter(hidden = true)
            Member member,
            @RequestBody MemberNicknameModifyRequest memberNicknameModifyRequest
    ) {
        // TODO: 닉네임 중복 검사 추가 - 409 status
        memberService.modifyNickname(memberNicknameModifyRequest, member);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "닉네임 조회", description = "회원의 현재 닉네임을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberNicknameResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/nickname")
    public ResponseEntity<MemberNicknameResponse> getNickname(
            @Parameter(hidden = true)
            Member member
    ) {
        MemberNicknameResponse memberNicknameResponse = memberService.getNickname(member);
        return ResponseEntity.ok(memberNicknameResponse);
    }

    @Operation(summary = "온보딩 정보 생성", description = "OAuth 인증이 완료된 회원의 온보딩 정보를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "온보딩 정보 생성 성공")
    @ApiResponse(responseCode = "400", description = "이미 온보딩된 계정", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "이미 온보딩", summary = "OauthAccount에 Member 이미 연결", value = "{\"code\":\"MEMBER_ALREADY_EXIST_IN_OAUTH_ACCOUNT\"}")
    }))
    @PostMapping
    public ResponseEntity<Void> create(
            @Parameter(hidden = true)
            OauthAccount oauthAccount,
            @RequestBody CreateMemberRequest createMemberRequest
    ) {
        memberService.create(oauthAccount, createMemberRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "온보딩 상태 확인", description = "회원의 온보딩 완료 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = OnboardingStatusResponse.class)))
    @GetMapping("/check/onboarding")
    public ResponseEntity<OnboardingStatusResponse> checkOnboardingStatus(
            @Parameter(hidden = true)
            OauthAccount oauthAccount
    ) {
        OnboardingStatusResponse onboardingStatusResponse = memberService.checkOnboardingStatus(oauthAccount);
        return ResponseEntity.ok(onboardingStatusResponse);
    }

    @Operation(summary = "사용자 금일 진행 정보 조회", description = "주어진 날짜(= 금일)의 음수량 달성 진행 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ProgressInfoResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/progress-info")
    public ResponseEntity<ProgressInfoResponse> getProgressInfo(
            @Parameter(hidden = true)
            Member member,
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", required = true, example = "2025-08-10")
            @RequestParam LocalDate date
    ) {
        ProgressInfoResponse progressInfoResponse = memberService.getProgressInfo(member, date);
        return ResponseEntity.ok().body(progressInfoResponse);
    }

    @PatchMapping("/notification/night")
    public ResponseEntity<Void> modifyIsNightNotificationAgreed(
            Member member,
            @RequestBody ModifyIsNightNotificationAgreedRequest modifyIsNightNotificationAgreedRequest
    ) {
        memberService.modifyIsNightNotificationAgreed(member, modifyIsNightNotificationAgreedRequest);
        return ResponseEntity.ok().build();
    }
}
