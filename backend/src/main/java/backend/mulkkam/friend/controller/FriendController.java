package backend.mulkkam.friend.controller;

import backend.mulkkam.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FriendController {

    private final FriendService friendService;
}
