package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 요청 수락 및 거절", description = "사용자에게 온 친구 요청을 수락 혹은 거절합니다.")
    @ApiResponse(responseCode = "200", description = "수락 및 거절 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 요청 id")
    @ApiResponse(responseCode = "401", description = "수락할 권한이 없는 사용자의 요청")
    @PostMapping("/process/{requestId}/{action:accept|reject}")
    public ResponseEntity<Void> processRequest(
            @PathVariable Long requestId,
            @PathVariable String action,
            @Parameter(hidden = true) MemberDetails memberDetails
    ) {
        boolean isAccept = action.equals("accept");

        friendService.processFriendRequest(requestId, isAccept, memberDetails);
        return ResponseEntity.noContent().build();
    }
}
