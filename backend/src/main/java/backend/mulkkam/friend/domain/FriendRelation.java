package backend.mulkkam.friend.domain;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_REQUEST;
import static backend.mulkkam.friend.domain.FriendRelationStatus.REQUESTED;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.common.exception.CommonException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE friend_relation SET deleted_at = NOW() WHERE id = ?")
@Entity
public class FriendRelation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requesterId;

    @Column(nullable = false)
    private Long addresseeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRelationStatus friendRelationStatus;

    private FriendRelation(Long id, Long requesterId, Long addresseeId, FriendRelationStatus status) {
        if (addresseeId.equals(requesterId)) {
            throw new CommonException(INVALID_FRIEND_REQUEST);
        }
        this.id = id;
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.friendRelationStatus = status;
    }

    public FriendRelation(
            Long requesterId,
            Long addresseeId,
            FriendRelationStatus friendRelationStatus
    ) {
        this(null, requesterId, addresseeId, friendRelationStatus);
    }

    public void updateAccepted() {
        this.friendRelationStatus = FriendRelationStatus.ACCEPTED;
    }

    public boolean isPending() {
        return friendRelationStatus == REQUESTED;
    }
}
