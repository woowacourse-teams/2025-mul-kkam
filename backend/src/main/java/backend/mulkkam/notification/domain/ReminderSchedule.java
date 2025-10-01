package backend.mulkkam.notification.domain;


import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE notification SET deleted_at = NOW() WHERE id = ?")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReminderSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private LocalTime schedule;
}
