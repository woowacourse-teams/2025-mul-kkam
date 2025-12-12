package backend.mulkkam.outboxnotification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.domain.OutboxNotification.Status;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationRepository;
import backend.mulkkam.support.fixture.DeviceFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("OutboxNotificationService 통합 테스트")
class OutboxNotificationServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private OutboxNotificationService outboxNotificationService;

    @Autowired
    private OutboxNotificationRepository outboxNotificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    private Member member1;
    private Member member2;
    private Device device1;
    private Device device2;
    private Device device3;

    @BeforeEach
    void setUp() {
        outboxNotificationRepository.deleteAll();

        member1 = memberRepository.save(
                MemberFixtureBuilder.builder()
                        .memberNickname(new MemberNickname("test1"))
                        .build()
        );
        member2 = memberRepository.save(
                MemberFixtureBuilder.builder()
                        .memberNickname(new MemberNickname("test2"))
                        .build()
        );

        device1 = deviceRepository.save(DeviceFixtureBuilder.withMember(member1).token("token-1").build());
        device2 = deviceRepository.save(DeviceFixtureBuilder.withMember(member1).token("token-2").build());
        device3 = deviceRepository.save(DeviceFixtureBuilder.withMember(member2).token("token-3").build());
    }

    @DisplayName("Outbox에 알림을 enqueue할 때")
    @Nested
    class EnqueueOutbox {

        @DisplayName("여러 멤버의 디바이스 토큰으로 outbox 레코드가 생성된다")
        @Test
        void success_createsOutboxRecordsForMultipleMemberDevices() {
            // given
            List<Long> memberIds = List.of(member1.getId(), member2.getId());
            LocalDateTime now = LocalDateTime.of(2025, 11, 18, 14, 30);

            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "물 마실 시간이에요",
                    "오늘도 건강한 하루 되세요!",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );

            // when
            outboxNotificationService.enqueueOutbox(memberIds, now, template);

            // then
            List<OutboxNotification> saved = outboxNotificationRepository.findAll();

            assertSoftly(softly -> {
                softly.assertThat(saved).hasSize(3);
                softly.assertThat(saved).allMatch(o -> o.getStatus() == Status.READY);
                softly.assertThat(saved).allMatch(o -> o.getAttemptCount() == 0);
                softly.assertThat(saved)
                        .extracting(OutboxNotification::getToken)
                        .containsExactlyInAnyOrder("token-1", "token-2", "token-3");
            });
        }

        @DisplayName("동일한 멱등키로 enqueue 시 중복 생성되지 않는다")
        @Test
        void success_ignoresDuplicateDedupeKey() {
            List<Long> memberIds = List.of(member1.getId());
            LocalDateTime now = LocalDateTime.of(2025, 11, 18, 14, 30);

            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "테스트 제목",
                    "테스트 내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );

            outboxNotificationService.enqueueOutbox(memberIds, now, template);
            outboxNotificationService.enqueueOutbox(memberIds, now, template);

            List<OutboxNotification> saved = outboxNotificationRepository.findAll();
            assertThat(saved).hasSize(2);
        }
    }
}
