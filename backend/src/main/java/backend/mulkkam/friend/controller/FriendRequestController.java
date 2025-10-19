package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.friend.dto.request.CreateFriendRequestRequest;
import backend.mulkkam.friend.dto.request.PatchFriendStatusRequest;
import backend.mulkkam.friend.dto.response.CreateFriendRequestResponse;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import backend.mulkkam.friend.service.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "친구 요청", description = "친구 요청 API")
@RequiredArgsConstructor
@RequestMapping("/friend-requests")
@RestController
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @Operation(summary = "친구 신청 생성", description = "다른 유저에게 친구를 신청합니다.")
    @ApiResponse(responseCode = "200", description = "친구 신청 성공", content = @Content(schema = @Schema(implementation = CreateFriendRequestResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "친구 신청자와 수신자 동일", value = "{\"code\":\"INVALID_FRIEND_REQUEST\"}")
    }))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 수신자 id", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "존재하지 않는 친구 신청 수신자", value = "{\"code\":\"NOT_FOUND_MEMBER\"}")
    }))
    @ApiResponse(responseCode = "409", description = "요청 내역이 이미 존재", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "서로 친구 신청을 보낸 이력 존재", value = "{\"code\":\"DUPLICATED_FRIEND_REQUEST\"}")
    }))
    @PostMapping
    public CreateFriendRequestResponse create(
            @Parameter(description = "친구 관계를 맺고싶은 멤버의 id", required = true)
            @RequestBody @Valid
            CreateFriendRequestRequest request,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        return friendRequestService.create(request, memberDetails);
    }

    @Operation(summary = "친구 신청 취소", description = "다른 유저에게 보낸 친구 신청을 취소합니다.")
    @ApiResponse(responseCode = "204", description = "친구 신청 취소 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "400", description = "취소가 불가능한 경우", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "이미 수락된 요청", value = "{\"code\":\"ALREADY_ACCEPTED\"}")
    }))
    @ApiResponse(responseCode = "404", description = "두 멤버 사이에 친구 신청이 존재하지 않는 경우", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "존재하지 않는 친구 신청", value = "{\"code\":\"NOT_FOUND_FRIEND_REQUEST\"}"),
            @ExampleObject(name = "친구 신청 수신자가 보낸 요청이 아님", value = "{\"code\":\"NOT_FOUND_FRIEND_REQUEST\"}")
    }))
    @DeleteMapping
    public ResponseEntity<Void> cancel(
            @Parameter(description = "취소하려는 요청 수신자의 멤버 id", required = true)
            @RequestParam("memberId") Long addresseeId,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        friendRequestService.cancel(addresseeId, memberDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "친구 요청 상태 변경 - 수락 / 거절", description = "사용자에게 온 친구 요청의 상태를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "친구 요청 상태 변경 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @ApiResponse(responseCode = "400", description = "취소가 불가능한 경우", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "이미 수락된 요청", value = "{\"code\":\"ALREADY_ACCEPTED\"}")
    }))
    @ApiResponse(responseCode = "404", description = "두 멤버 사이에 친구 신청이 존재하지 않는 경우", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = {
            @ExampleObject(name = "존재하지 않는 친구 신청", value = "{\"code\":\"NOT_FOUND_FRIEND_REQUEST\"}"),
            @ExampleObject(name = "친구 신청 수신자가 보낸 요청이 아님", value = "{\"code\":\"NOT_FOUND_FRIEND_REQUEST\"}")
    }))
    @PatchMapping
    public void update(
            @RequestBody @Valid
            PatchFriendStatusRequest request,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        friendRequestService.modifyFriendStatus(request, memberDetails);
    }

    @Operation(summary = "받은 친구 신청 목록", description = "내가 받은 친구 신청 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReadReceivedFriendRelationResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/received")
    public ReadReceivedFriendRelationResponse getReceived(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "lastId, 첫 요청시 null", required = false)
            @RequestParam(required = false) Long lastId,
            @Parameter(description = "size, 미지정시 20", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return friendRequestService.readReceived(memberDetails, lastId, size);
    }

    @Operation(summary = "받은 친구 신청 목록 갯수 조회", description = "내가 받은 친구 신청 목록의 갯수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GetReceivedFriendRequestCountResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/received-count")
    public GetReceivedFriendRequestCountResponse getReceivedCount(
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        return friendRequestService.getReceivedCount(memberDetails);
    }

    @Operation(summary = "보낸 친구 신청 목록", description = "내가 보낸 친구 신청 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ReadSentFriendRelationResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @GetMapping("/sent")
    public ReadSentFriendRelationResponse getSent(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @Parameter(description = "lastId, 첫 요청시 null", required = false)
            @RequestParam(required = false) Long lastId,
            @Parameter(description = "size, 미지정시 20", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return friendRequestService.readSent(memberDetails, lastId, size);
    }
}
