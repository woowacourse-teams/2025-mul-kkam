package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.friend.dto.request.CreateFriendRequestRequest;
import backend.mulkkam.friend.dto.response.CreateFriendRequestResponse;
import backend.mulkkam.friend.dto.request.PatchFriendStatusRequest;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRelationResponse;
import backend.mulkkam.friend.service.FriendRequestService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "친구 요청", description = "친구 요청 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @Operation(summary = "친구 신청 생성", description = "다른 유저에게 친구를 신청합니다.")
    @PostMapping
    public CreateFriendRequestResponse createFriendRequest(
            @Parameter(description = "친구 관계를 맺고싶은 멤버의 id", required = true)
            @RequestBody @Valid CreateFriendRequestRequest request,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        return friendRequestService.create(request, memberDetails);
    }

    @Operation(summary = "친구 신청 취소", description = "다른 유저에게 보낸 친구 신청을 취소합니다.")
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> cancelFriendRequest(
            @Parameter(description = "취소하려는 요청의 id", required = true)
            @PathVariable Long requestId,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        friendRequestService.cancel(requestId, memberDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "친구 요청 상태 변경 - 수락 / 거절", description = "사용자에게 온 친구 요청의 상태를 변경합니다.")
    @PatchMapping("/{requestId}")
    public void updateFriendRequest(
            @Parameter(description = "취소하려는 요청의 id", required = true)
            @PathVariable Long requestId,
            @RequestBody @Valid PatchFriendStatusRequest request,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendRequestService.modifyFriendStatus(requestId, request, memberDetails);
    }

    @Operation(summary = "받은 친구 신청 목록", description = "내가 받은 친구 신청 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReadReceivedFriendRelationResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/received")
    public ReadReceivedFriendRelationResponse getReceivedFriendRequests(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "lastId, 첫 요청시 null", required = false)
            @RequestParam(required = false) Long lastId,
            @Parameter(description = "size", required = true)
            @RequestParam(defaultValue = "20") int size
    ) {
        return friendRequestService.readReceivedFriendRequests(memberDetails, lastId, size);
    }

    @Operation(summary = "받은 친구 신청 목록 갯수 조회", description = "내가 받은 친구 신청 목록의 갯수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GetReceivedFriendRequestCountResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/received-count")
    public GetReceivedFriendRequestCountResponse getReceivedFriendRequestCount(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        return friendRequestService.getReceivedFriendRequestCount(memberDetails);
    }
}
