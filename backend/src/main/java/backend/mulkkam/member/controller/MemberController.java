package backend.mulkkam.member.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/members")
public class MemberController {

    @PostMapping("/weight")
    public void createWeight() {

    }
}
