package backend.mulkkam.admin.controller;

import backend.mulkkam.admin.dto.request.UpdateAdminMemberRequest;
import backend.mulkkam.admin.dto.response.CheckAdminResponse;
import backend.mulkkam.admin.dto.response.GetAdminMemberDetailResponse;
import backend.mulkkam.admin.dto.response.GetAdminMemberListResponse;
import backend.mulkkam.admin.service.AdminMemberService;
import backend.mulkkam.common.auth.annotation.AuthLevel;
import backend.mulkkam.common.auth.annotation.RequireAuth;
import backend.mulkkam.common.dto.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 - 회원 관리", description = "어드민 회원 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @Operation(summary = "어드민 권한 확인", description = "현재 로그인한 사용자가 어드민인지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "성공 응답")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @RequireAuth(level = AuthLevel.MEMBER)
    @GetMapping("/check")
    public CheckAdminResponse checkAdmin(
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        return CheckAdminResponse.from(memberDetails);
    }

    @Operation(summary = "회원 목록 조회", description = "전체 회원 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @RequireAuth(level = AuthLevel.ADMIN)
    @GetMapping("/members")
    public Page<GetAdminMemberListResponse> getMembers(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return adminMemberService.getMembers(pageable);
    }

    @Operation(summary = "회원 상세 조회", description = "특정 회원의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    @RequireAuth(level = AuthLevel.ADMIN)
    @GetMapping("/members/{memberId}")
    public GetAdminMemberDetailResponse getMember(
            @PathVariable Long memberId
    ) {
        return adminMemberService.getMember(memberId);
    }

    @Operation(summary = "회원 정보 수정", description = "특정 회원의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    @RequireAuth(level = AuthLevel.ADMIN)
    @PutMapping("/members/{memberId}")
    public GetAdminMemberDetailResponse updateMember(
            @PathVariable Long memberId,
            @RequestBody UpdateAdminMemberRequest request
    ) {
        return adminMemberService.updateMember(memberId, request);
    }

    @Operation(summary = "회원 삭제", description = "특정 회원을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    @RequireAuth(level = AuthLevel.ADMIN)
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long memberId
    ) {
        adminMemberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
