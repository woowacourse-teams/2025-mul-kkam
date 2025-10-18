package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.dto.FriendRelationResponse;
import backend.mulkkam.friend.service.FriendRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "친구", description = "친구 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendRelationService friendRelationService;

    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    @DeleteMapping("/{friendRelationId}")
    public ResponseEntity<Void> deleteFriend(
            @Parameter(description = "삭제할 친구 관계 ID", required = true)
            @PathVariable Long friendRelationId,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        friendRelationService.delete(friendRelationId, memberDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "친구 요청 거절", description = "사용자에게 온 친구 요청을 거절합니다.")
    @ApiResponse(responseCode = "200", description = "거절 성공")
    @ApiResponse(responseCode = "400", description = "올바르지 않은 친구 관계 id")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 관계 id")
    @ApiResponse(responseCode = "403", description = "거절할 권한이 없는 사용자의 요청")
    @PostMapping("request/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendRelationService.rejectFriendRequest(requestId, memberDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 요청 수락", description = "사용자에게 온 친구 요청을 수락합니다.")
    @ApiResponse(responseCode = "200", description = "수락 성공")
    @ApiResponse(responseCode = "400", description = "올바르지 않은 친구 관계 id")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 관계 id")
    @ApiResponse(responseCode = "409", description = "이미 친구 관계가 존재하는 경우에 대한 요청")
    @PostMapping("request/{requestId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendRelationService.acceptFriend(requestId, memberDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 목록 조회", description = "사용자의 친구 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<FriendRelationResponse> readFriendRelationsInStatusAccepted(
            @Parameter(description = "커서 lastId(최초 요청시 생략)")
            @RequestParam(required = false) Long lastId,
            @Parameter(description = "size 값", required = true, example = "5")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
                friendRelationService.readFriendRelationsInStatusAccepted(lastId, size, memberDetails));
    }
}
