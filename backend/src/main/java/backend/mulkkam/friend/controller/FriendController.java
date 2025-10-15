package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Operation(summary = "친구 요청 거절", description = "사용자에게 온 친구 요청을 거절합니다.")
    @ApiResponse(responseCode = "200", description = "거절 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 요청 id")
    @ApiResponse(responseCode = "403", description = "거절할 권한이 없는 사용자의 요청")
    @PostMapping("request/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendService.rejectFriendRequest(requestId, memberDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 요청 수락", description = "사용자에게 온 친구 요청을 수락합니다.")
    @ApiResponse(responseCode = "200", description = "수락 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 요청 id")
    @ApiResponse(responseCode = "403", description = "수락할 권한이 없는 사용자의 요청")
    @ApiResponse(responseCode = "409", description = "이미 친구 관계가 존재하는 경우에 대한 요청")
    @PostMapping("request/{requestId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendService.acceptFriendRequest(requestId, memberDetails);
        return ResponseEntity.ok().build();
    }
}
