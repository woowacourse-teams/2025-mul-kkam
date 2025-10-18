package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import backend.mulkkam.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @DeleteMapping("/{friendRelationId}")
    public ResponseEntity<Void> deleteFriend(
            @Parameter(description = "삭제할 친구 관계 ID", required = true)
            @PathVariable Long friendRelationId,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        friendService.deleteFriend(friendRelationId, memberDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "친구 목록 조회", description = "사용자의 친구 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public FriendRelationResponse readFriendRelationsInStatusAccepted(
            @Parameter(description = "커서 lastId(최초 요청시 생략)")
            @RequestParam(required = false) Long lastId,
            @Parameter(description = "size 값", required = true, example = "5")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        return friendService.readFriendRelationsInStatusAccepted(lastId, size, memberDetails);
    }

    @Operation(summary = "보낸 친구 신청 목록", description = "내가 보낸 친구 신청 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReadSentFriendRelationResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/requests/sent")
    public ResponseEntity<ReadSentFriendRelationResponse> getSentFriendRequests(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "lastId, 첫 요청시 null")
            @RequestParam(required = false) Long lastId,
            @Parameter(description = "size", required = true)
            @RequestParam(defaultValue = "20") int size
    ) {
        ReadSentFriendRelationResponse readSentFriendRelationResponse = friendService.readSentFriendRelations(
                memberDetails, lastId, size);
        return ResponseEntity.ok(readSentFriendRelationResponse);
    }
}
