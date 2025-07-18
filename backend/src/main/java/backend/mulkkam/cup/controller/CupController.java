package backend.mulkkam.cup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CupController {

    @GetMapping("/cups")
    public void read() {

    }

    @PostMapping("/cup")
    public void create() {

    }
}
