package backend.mulkkam.device.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE device SET deleted_at = NOW() WHERE id = ?")
@Entity
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column
    private String deviceUuid;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public Device(String token, String deviceUuid, Member member) {
        this.token = token;
        this.deviceUuid = deviceUuid;
        this.member = member;
    }

    public void modifyToken(String token) {
        this.token = token;
    }

    public void nullifyToken() {
        this.token = null;
    }
}
