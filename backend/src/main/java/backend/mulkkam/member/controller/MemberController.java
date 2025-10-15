package backend.mulkkam.member.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.ModifyIsMarketingNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsNightNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsReminderEnabledRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.dto.response.MemberSearchResponse;
import backend.mulkkam.member.dto.response.NotificationSettingsResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
            MemberDetails memberDetails
    ) {
        MemberResponse memberResponse = memberService.get(memberDetails);
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
            MemberDetails memberDetails,
            @RequestBody PhysicalAttributesModifyRequest physicalAttributesModifyRequest
    ) {
        memberService.modifyPhysicalAttributes(
                physicalAttributesModifyRequest,
                memberDetails
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
            MemberDetails memberDetails,
            @Parameter(description = "검사할 닉네임", required = true, example = "밍곰")
            @RequestParam String nickname
    ) {
        memberService.validateDuplicateNickname(
                nickname,
                memberDetails
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
            MemberDetails memberDetails,
            @RequestBody MemberNicknameModifyRequest memberNicknameModifyRequest
    ) {
        // TODO: 닉네임 중복 검사 추가 - 409 status
        memberService.modifyNickname(memberNicknameModifyRequest, memberDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "닉네임 조회", description = "회원의 현재 닉네임을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberNicknameResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/nickname")
    public ResponseEntity<MemberNicknameResponse> getNickname(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        MemberNicknameResponse memberNicknameResponse = memberService.getNickname(memberDetails);
        return ResponseEntity.ok(memberNicknameResponse);
    }

    @Operation(summary = "사용자 금일 진행 정보 조회", description = "주어진 날짜(= 금일)의 음용량 달성 진행 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ProgressInfoResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/progress-info")
    public ResponseEntity<ProgressInfoResponse> getProgressInfo(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", required = true, example = "2025-08-10")
            @RequestParam LocalDate date
    ) {
        ProgressInfoResponse progressInfoResponse = memberService.getProgressInfo(memberDetails, date);
        return ResponseEntity.ok().body(progressInfoResponse);
    }

    @Operation(summary = "사용자 야간 알림 수신 정보 수정", description = "야간 알림 수신 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "야간 알림 반영 성공")
    @PatchMapping("/notifications/night")
    public ResponseEntity<Void> modifyIsNightNotificationAgreed(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "boolean 값", required = true, example = "true")
            @RequestBody ModifyIsNightNotificationAgreedRequest modifyIsNightNotificationAgreedRequest
    ) {
        memberService.modifyIsNightNotificationAgreed(memberDetails, modifyIsNightNotificationAgreedRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 마케팅 알림 수신 정보 수정", description = "마케팅 알림 수신 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "마케팅 알림 반영 성공")
    @PatchMapping("/notifications/marketing")
    public ResponseEntity<Void> modifyIsMarketingNotificationAgreed(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "boolean 값", required = true, example = "true")
            @RequestBody ModifyIsMarketingNotificationAgreedRequest modifyIsMarketingNotificationAgreed
    ) {
        memberService.modifyIsMarketingNotificationAgreed(memberDetails, modifyIsMarketingNotificationAgreed);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 알림 수신 정보 조회", description = "야간/마케팅 알림 수신 여부를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "알림 수신 여부 조회 성공")
    @GetMapping("/notifications/settings")
    public ResponseEntity<NotificationSettingsResponse> getNotificationSettings(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        NotificationSettingsResponse notificationSettingsResponse = memberService.getNotificationSettings(
                memberDetails);
        return ResponseEntity.ok(notificationSettingsResponse);
    }

    @Operation(summary = "사용자 리마인더 스케쥴링 정보 수정", description = "리마인더 스케쥴링 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "반영 성공")
    @PatchMapping("/reminder")
    public ResponseEntity<Void> modifyIsReminderEnabled(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "boolean 값", required = true, example = "true")
            @RequestBody ModifyIsReminderEnabledRequest modifyIsReminderEnabledRequest
    ) {
        memberService.modifyIsReminderEnabled(memberDetails, modifyIsReminderEnabledRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 탈퇴", description = "회원을 탈퇴합니다")
    @ApiResponse(responseCode = "200", description = "탈퇴 성공")
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        memberService.delete(memberDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 닉네임 검색", description = "사용자 닉네임을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    public ResponseEntity<MemberSearchResponse> search(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "검색 할 내용", required = true, example = "돈까스먹는환")
            @RequestParam String prefix,
            @Parameter(description = "page 값", required = true, example = "4")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "size 값", required = true, example = "5")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(memberService.searchMember(memberDetails, prefix, page, size));
    }
}
