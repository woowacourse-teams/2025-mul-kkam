package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.friend.dto.request.SendFriendReminderRequest;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import backend.mulkkam.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "친구", description = "친구 API")
@RequiredArgsConstructor
@RequestMapping("/friends")
@RestController
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "친구 삭제 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @DeleteMapping
    public ResponseEntity<Void> deleteFriend(
            @Parameter(description = "삭제할 친구의 멤버 ID", required = true)
            @RequestParam Long memberId,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        friendService.delete(memberId, memberDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "친구 목록 조회", description = "사용자의 친구 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공", content = @Content(schema = @Schema(implementation = FriendRelationResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping
    public FriendRelationResponse readFriendRelationsInStatusAccepted(
            @Parameter(description = "커서 lastId(최초 요청시 생략)")
            @RequestParam(required = false) Long lastId,
            @Parameter(description = "size 값, 미지정시 10", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        return friendService.read(lastId, size, memberDetails);
    }

    @Operation(summary = "친구에게 물풍선 던지기", description = "사용자 친구에게 리마인드 알림을 보냅니다.")
    @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공", content = @Content(schema = @Schema(implementation = FriendRelationResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PostMapping("/reminder")
    public void sendReminder(
            @Parameter(description = "물풍선 보내기 요청 body")
            @Valid SendFriendReminderRequest request,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {

    }
}
