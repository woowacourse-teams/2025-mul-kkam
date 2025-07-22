package backend.mulkkam.member.controller;

import backend.mulkkam.member.dto.MemberPhysicalAttributesModifyRequest;
import backend.mulkkam.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/weight")
    public ResponseEntity<Void> createWeight(@RequestBody MemberPhysicalAttributesModifyRequest memberPhysicalAttributesModifyRequest) {
        memberService.modifyPhysicalAttributes(
                memberPhysicalAttributesModifyRequest,
                1L
        );
        return ResponseEntity.noContent().build();
    }
}
