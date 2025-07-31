package backend.mulkkam.member.controller;

import backend.mulkkam.member.dto.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<Void> modifyPhysicalAttributes(@RequestBody PhysicalAttributesModifyRequest physicalAttributesModifyRequest) {
        memberService.modifyPhysicalAttributes(
                physicalAttributesModifyRequest,
                1L
        );
        return ResponseEntity.ok().build();
    }
}
