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
    private Member member3;
    private Device device1;
    private Device device2;
    private Device device3;

    @BeforeEach
    void setUp() {
        outboxNotificationRepository.deleteAll();
        member1 = memberRepository.save(MemberFixtureBuilder
                .builder()
                .memberNickname(new MemberNickname("test1"))
                .build());
        member2 = memberRepository.save(MemberFixtureBuilder
                .builder()
                .memberNickname(new MemberNickname("test2"))
                .build());
        member3 = memberRepository.save(MemberFixtureBuilder
                .builder()
                .memberNickname(new MemberNickname("test3"))
                .build());

        device1 = deviceRepository.save(
                DeviceFixtureBuilder.withMember(member1).token("token-1").build()
        );
        device2 = deviceRepository.save(
                DeviceFixtureBuilder.withMember(member1).token("token-2").build()
        );
        device3 = deviceRepository.save(
                DeviceFixtureBuilder.withMember(member2).token("token-3").build()
        );
    }

    @DisplayName("Outbox에 알림을 enqueue할 때")
    @Nested
    class EnqueueOutbox {

        @DisplayName("여러 멤버의 디바이스 토큰으로 outbox 레코드가 생성된다")
        @Test
        void success_createsOutboxRecordsForMultipleMemberDevices() {
            // given
            List<Long> memberIds = List.of(member1.getId(), member2.getId(), member3.getId());
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
            List<OutboxNotification> savedOutboxes = outboxNotificationRepository.findAll();

            assertSoftly(softly -> {
                softly.assertThat(savedOutboxes).hasSize(3);
                softly.assertThat(savedOutboxes)
                        .allMatch(outbox -> outbox.getType().equals("REMIND"));
                softly.assertThat(savedOutboxes)
                        .allMatch(outbox -> outbox.getStatus() == Status.READY);
                softly.assertThat(savedOutboxes)
                        .allMatch(outbox -> outbox.getTitle().equals("물 마실 시간이에요"));
                softly.assertThat(savedOutboxes)
                        .allMatch(outbox -> outbox.getBody().equals("오늘도 건강한 하루 되세요!"));
                softly.assertThat(savedOutboxes)
                        .allMatch(outbox -> outbox.getAttemptCount() == 0);
                softly.assertThat(savedOutboxes)
                        .allMatch(outbox -> outbox.getNextAttemptAt() == null);
            });
        }

        @DisplayName("한 멤버가 여러 디바이스를 가진 경우 모든 토큰에 대해 outbox가 생성된다")
        @Test
        void success_createsOutboxForAllDevicesOfSingleMember() {
            // given
            List<Long> memberIds = List.of(member1.getId());
            LocalDateTime now = LocalDateTime.of(2025, 11, 18, 14, 30);
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "테스트 제목",
                    "테스트 내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );

            // when
            outboxNotificationService.enqueueOutbox(memberIds, now, template);

            // then
            List<OutboxNotification> savedOutboxes = outboxNotificationRepository.findAll();

            assertSoftly(softly -> {
                softly.assertThat(savedOutboxes).hasSize(2);
                softly.assertThat(savedOutboxes)
                        .extracting(OutboxNotification::getMemberId)
                        .containsOnly(member1.getId());
                softly.assertThat(savedOutboxes)
                        .extracting(OutboxNotification::getToken)
                        .containsExactlyInAnyOrder("token-1", "token-2");
            });
        }

        @DisplayName("동일한 dedupe_key로 중복 enqueue 시 무시된다")
        @Test
        void success_ignoresDuplicateDedupeKey() {
            // given
            List<Long> memberIds = List.of(member1.getId());
            LocalDateTime now = LocalDateTime.of(2025, 11, 18, 14, 30);
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "테스트 제목",
                    "테스트 내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );

            // when
            outboxNotificationService.enqueueOutbox(memberIds, now, template);
            outboxNotificationService.enqueueOutbox(memberIds, now, template);

            // then
            List<OutboxNotification> savedOutboxes = outboxNotificationRepository.findAll();
            assertThat(savedOutboxes).hasSize(2);
        }

        @DisplayName("success_멤버에게 등록된 디바이스가 없으면 outbox가 생성되지 않는다")
        @Test
        void success_noOutboxCreatedWhenMemberHasNoDevices() {
            // given
            Member memberWithoutDevice = memberRepository.save(
                    MemberFixtureBuilder.builder().build()
            );
            List<Long> memberIds = List.of(memberWithoutDevice.getId());
            LocalDateTime now = LocalDateTime.of(2025, 11, 18, 14, 30);
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "테스트 제목",
                    "테스트 내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );

            // when
            outboxNotificationService.enqueueOutbox(memberIds, now, template);

            // then
            List<OutboxNotification> savedOutboxes = outboxNotificationRepository.findAll();
            assertThat(savedOutboxes).isEmpty();
        }

        @DisplayName("다른 시간으로 enqueue 시 별도의 outbox가 생성된다")
        @Test
        void success_separateOutboxForDifferentTime() {
            // given
            List<Long> memberIds = List.of(member1.getId(), member2.getId());
            LocalDateTime time1 = LocalDateTime.of(2025, 11, 18, 14, 30);
            LocalDateTime time2 = LocalDateTime.of(2025, 11, 18, 15, 30);
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "테스트 제목",
                    "테스트 내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );

            // when
            outboxNotificationService.enqueueOutbox(memberIds, time1, template);
            outboxNotificationService.enqueueOutbox(memberIds, time2, template);

            // then
            List<OutboxNotification> savedOutboxes = outboxNotificationRepository.findAll();
            List<String> dedupeKeys = savedOutboxes
                    .stream()
                    .map(OutboxNotification::getDedupeKey)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(dedupeKeys).hasSize(6);
                softly.assertThat(dedupeKeys)
                        .contains("REMIND:" + member1.getId() + ":" + time1 + ":" + device1.getToken());
                softly.assertThat(dedupeKeys)
                        .contains("REMIND:" + member1.getId() + ":" + time2 + ":" + device2.getToken());
                softly.assertThat(dedupeKeys)
                        .contains("REMIND:" + member2.getId() + ":" + time1 + ":" + device3.getToken());
            });
        }
    }

    @DisplayName("Dedupe key를 생성할 때")
    @Nested
    class BuildDedupeKey {

        @DisplayName("올바른 형식의 dedupe key가 생성된다")
        @Test
        void success_generatesCorrectFormat() {
            // given
            String type = "REMIND";
            Long memberId = 123L;
            LocalDateTime time = LocalDateTime.of(2025, 11, 18, 14, 30);

            // when
            String dedupeKey = outboxNotificationService.buildDedupeKey(type, memberId, time, "token");

            // then
            assertThat(dedupeKey).isEqualTo("REMIND:123:2025-11-18T14:30:token");
        }
    }
}
