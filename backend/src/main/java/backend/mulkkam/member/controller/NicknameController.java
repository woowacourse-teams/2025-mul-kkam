package backend.mulkkam.member.controller;

import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "닉네임", description = "닉네임 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/nickname")
public class NicknameController {

    private final MemberService memberService;

    @Operation(summary = "닉네임 중복 검사", description = "회원가입 시 사용할 닉네임의 중복 여부를 검사합니다.")
    @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임")
    @ApiResponse(responseCode = "400", description = "잘못된 닉네임 형식", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "형식 오류", summary = "길이 제약 위반", value = "{\"code\":\"INVALID_MEMBER_NICKNAME\"}")
    }))
    @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "중복 닉네임", summary = "이미 존재", value = "{\"code\":\"DUPLICATE_MEMBER_NICKNAME\"}")
    }))
    @GetMapping("/validation")
    public ResponseEntity<Void> checkForDuplicates(
            @Parameter(description = "검사할 닉네임", required = true, example = "밍곰")
            @RequestParam String nickname
    ) {
        memberService.validateDuplicateNickname(nickname);
        return ResponseEntity.ok().build();
    }
}
