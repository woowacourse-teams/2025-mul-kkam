package backend.mulkkam.friend.domain;

import backend.mulkkam.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@AllArgsConstructor
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
    private FriendStatus friendStatus;

    public FriendRelation(
            Long requesterId,
            Long addresseeId,
            FriendStatus friendStatus
    ) {
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.friendStatus = friendStatus;
    }

    public boolean isAddressee(Long id) {
        if (id.equals(addresseeId)) {
            return true;
        }
        return false;
    }

    public void updateAccepted() {
        this.friendStatus = FriendStatus.ACCEPTED;
    }
}
