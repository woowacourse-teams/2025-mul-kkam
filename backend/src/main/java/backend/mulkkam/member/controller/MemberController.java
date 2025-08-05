package backend.mulkkam.member.controller;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.OnboardingStatusResponse;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> get(@PathVariable Long memberId) {
        MemberResponse memberResponse = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping("/physical-attributes")
    public ResponseEntity<Void> modifyPhysicalAttributes(
            @RequestBody PhysicalAttributesModifyRequest physicalAttributesModifyRequest
    ) {
        memberService.modifyPhysicalAttributes(
                physicalAttributesModifyRequest,
                1L
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nickname/validation")
    public ResponseEntity<Void> checkForDuplicates(@RequestParam String nickname) {
        memberService.validateDuplicateNickname(
                nickname,
                1L
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/nickname")
    public ResponseEntity<Void> modifyNickname(@RequestBody MemberNicknameModifyRequest memberNicknameModifyRequest) {
        memberService.modifyNickname(memberNicknameModifyRequest, 1L);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nickname")
    public ResponseEntity<MemberNicknameResponse> getNickname() {
        MemberNicknameResponse memberNicknameResponse = memberService.getNickname(1L);
        return ResponseEntity.ok(memberNicknameResponse);
    }

    @PostMapping
    public ResponseEntity<MemberNicknameResponse> create(
            OauthAccount oauthAccount,
            @RequestBody CreateMemberRequest createMemberRequest) {
        memberService.create(oauthAccount, createMemberRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/onboarding")
    public ResponseEntity<OnboardingStatusResponse> checkOnboardingStatus(OauthAccount oauthAccount) {
        OnboardingStatusResponse onboardingStatusResponse = memberService.checkOnboardingStatus(oauthAccount);
        return ResponseEntity.ok(onboardingStatusResponse);
    }
}
