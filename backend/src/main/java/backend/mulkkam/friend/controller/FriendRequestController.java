package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.dto.CreateFriendRequestRequest;
import backend.mulkkam.friend.dto.PatchFriendStatusRequest;
import backend.mulkkam.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "친구 요청", description = "친구 요청 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {

    private final FriendService friendService;

    @Operation(summary = "친구 신청 생성", description = "다른 유저에게 친구를 신청합니다.")
    @PostMapping
    public ResponseEntity<Void> createFriendRequest(
            @Parameter(description = "친구 관계를 맺고싶은 멤버의 id", required = true)
            @RequestBody CreateFriendRequestRequest request,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        return ResponseEntity.created(URI.create("resource uri")).build();
    }

    @Operation(summary = "친구 신청 취소", description = "다른 유저에게 보낸 친구 신청을 취소합니다.")
    @DeleteMapping("/{requestId}")
    public void deleteFriendRequest(
            @Parameter(description = "취소하려는 요청의 id", required = true)
            @PathVariable Long requestId,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {

    }

    @Operation(summary = "친구 요청 상태 변경 - 수락 / 거절", description = "사용자에게 온 친구 요청의 상태를 변경합니다.")
    @PatchMapping("/{requestId}")
    public void updateFriendRequest(
            @Parameter(description = "취소하려는 요청의 id", required = true)
            @PathVariable Long requestId,
            @RequestBody PatchFriendStatusRequest request,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendService.modifyFriendStatus(requestId, request, memberDetails);
    }

    @Deprecated
    @Operation(summary = "친구 요청 거절", description = "사용자에게 온 친구 요청을 거절합니다.")
    @ApiResponse(responseCode = "200", description = "거절 성공")
    @ApiResponse(responseCode = "400", description = "올바르지 않은 친구 관계 id")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 관계 id")
    @ApiResponse(responseCode = "403", description = "거절할 권한이 없는 사용자의 요청")
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendService.rejectFriendRequest(requestId, memberDetails);
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @Operation(summary = "친구 요청 수락", description = "사용자에게 온 친구 요청을 수락합니다.")
    @ApiResponse(responseCode = "200", description = "수락 성공")
    @ApiResponse(responseCode = "400", description = "올바르지 않은 친구 관계 id")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 관계 id")
    @ApiResponse(responseCode = "409", description = "이미 친구 관계가 존재하는 경우에 대한 요청")
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendService.acceptFriend(requestId, memberDetails);
        return ResponseEntity.ok().build();
    }
}
