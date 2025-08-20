package backend.mulkkam.cup.controller;

import backend.mulkkam.cup.dto.CupEmojisResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.service.CupEmojiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("cup-emoji")
public class CupEmojiController {

    private final CupEmojiService cupEmojiService;

    @Operation(summary = "컵 이모지 리스트 반환", description = "컵 이모지 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공 응답", content = @Content(schema = @Schema(implementation = CupsResponse.class)))
    @GetMapping
    public ResponseEntity<CupEmojisResponse> readAll() {
        return ResponseEntity.ok(cupEmojiService.readAll());
    }
}
