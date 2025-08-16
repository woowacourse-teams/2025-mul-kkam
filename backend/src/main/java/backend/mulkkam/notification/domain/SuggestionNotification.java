package backend.mulkkam.notification.domain;

import backend.mulkkam.member.domain.vo.TargetAmount;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "recommended_target_amount"))
    private TargetAmount recommendedTargetAmount;

    @Column
    private boolean applyTargetAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Notification notification;

    public SuggestionNotification(TargetAmount recommendedTargetAmount, boolean applyTargetAmount,
                                  Notification notification) {
        this.recommendedTargetAmount = recommendedTargetAmount;
        this.applyTargetAmount = applyTargetAmount;
        this.notification = notification;
    }
}
