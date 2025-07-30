package backend.mulkkam.member.controller;

import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

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
}
