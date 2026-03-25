package backend.mulkkam.device.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column
    private String deviceUuid;

    @Enumerated(EnumType.STRING)
    @Column
    private DevicePlatform platform;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public Device(String token, String deviceUuid, Member member, DevicePlatform platform) {
        this.token = token;
        this.deviceUuid = deviceUuid;
        this.platform = platform;
        this.member = member;
    }

    public void modifyToken(String token) {
        this.token = token;
    }

    public void modifyPlatform(DevicePlatform platform) {
        this.platform = platform;
    }
}
