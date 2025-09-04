package backend.mulkkam.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class SuggestionNotification {

    @Id
    private Long id;
    
    @Column(nullable = false)
    private int recommendedTargetAmount;

    @Column(nullable = false)
    private boolean applyTargetAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Notification notification;

    public SuggestionNotification(
            int recommendedTargetAmount,
            boolean applyTargetAmount,
            Notification notification
    ) {
        this.recommendedTargetAmount = recommendedTargetAmount;
        this.applyTargetAmount = applyTargetAmount;
        this.notification = notification;
    }

    public void updateApplyTargetAmount(boolean applyTargetAmount) {
        this.applyTargetAmount = applyTargetAmount;
    }
}
