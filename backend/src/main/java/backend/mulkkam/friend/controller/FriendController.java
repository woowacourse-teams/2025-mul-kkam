package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.service.FriendCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "친구", description = "친구 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendCommandService friendCommandService;

    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    @DeleteMapping("/{friendRelationId}")
    public ResponseEntity<Void> deleteFriend(
            @Parameter(description = "삭제할 친구 관계 ID", required = true)
            @PathVariable Long friendRelationId,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        friendCommandService.deleteFriend(friendRelationId, memberDetails);
        return ResponseEntity.noContent().build();
    }
}
