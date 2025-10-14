package backend.mulkkam.friend.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
