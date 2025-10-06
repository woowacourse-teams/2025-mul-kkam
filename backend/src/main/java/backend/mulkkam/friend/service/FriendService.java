package backend.mulkkam.friend.service;

import backend.mulkkam.friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FriendService {

    private final FriendRepository friendRepository;
}
