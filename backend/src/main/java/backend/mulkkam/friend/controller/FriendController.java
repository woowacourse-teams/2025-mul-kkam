package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRequestsResponse;
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
@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 삭제", description = "로그인 멤버와 지정 멤버 사이의 친구 관계를 삭제합니다.")
    @DeleteMapping("/{memberId}")
    public void deleteFriend(
            @Parameter(description = "삭제할 친구의 id", required = true)
            @PathVariable Long memberId,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        friendService.delete(memberId, memberDetails);
    }

    @Operation(summary = "받은 친구 신청 목록", description = "내가 받은 친구 신청 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReadReceivedFriendRequestsResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/requests/received")
    public ResponseEntity<ReadReceivedFriendRequestsResponse> getReceivedFriendRequests(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int size
    ) {
        ReadReceivedFriendRequestsResponse readReceivedFriendRequestsResponse = friendService.readReceivedFriendRequests(
                memberDetails, lastId, size);
        return ResponseEntity.ok(readReceivedFriendRequestsResponse);
    }

    @GetMapping("/requests/received/count")
    public ResponseEntity<GetReceivedFriendRequestCountResponse> getReceivedFriendRequestCount(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(friendService.getReceivedFriendRequestCount(memberDetails));
    }
}
