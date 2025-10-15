package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
}
