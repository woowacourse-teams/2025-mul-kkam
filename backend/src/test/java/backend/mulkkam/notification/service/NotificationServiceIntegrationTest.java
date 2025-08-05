package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.ReadNotificationResponse;
import backend.mulkkam.notification.dto.ReadNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.NotificationFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationServiceIntegrationTest extends ServiceIntegrationTest {

    private static final int DAY_LIMIT = 7;
    private final LocalDateTime localDateTime = LocalDateTime.of(2025, 8, 6, 10, 10);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private Member savedMember;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder.builder().build();
        savedMember = memberRepository.save(member);
    }

    @DisplayName("알림 조회 기능을 사용할 때")
    @Nested
    class GetNotificationsAfter {

        @DisplayName("요청 날짜로부터 7일 내의 최신순으로 데이터만이 불러와진다")
        @Test
        void success_returnsNotificationsWithin7DaysSortedByLatest() {
            // given
            List<Notification> notifications = List.of(
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 1))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 2))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 3))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 4))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 5))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 6))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 7))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 8))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 9))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 10))
                            .build()
            );
            notificationRepository.saveAll(notifications);

            LocalDateTime localDateTime = LocalDateTime.of(2025, 8, 7, 10, 10);
            LocalDateTime limitStartDateTime = localDateTime.minusDays(DAY_LIMIT);
            int size = 5;
            ReadNotificationsRequest readNotificationsRequest = new ReadNotificationsRequest(10L, localDateTime, size);

            // when
            ReadNotificationsResponse readNotificationsResponse = notificationService.getNotificationsAfter(
                    readNotificationsRequest);

            // then
            int actualSize = readNotificationsResponse.readNotificationResponses().size();
            List<ReadNotificationResponse> actualReadNotificationResponses = readNotificationsResponse.readNotificationResponses();
            List<LocalDateTime> createdAts = readNotificationsResponse.readNotificationResponses().stream()
                    .map(ReadNotificationResponse::createdAt)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(actualSize).isEqualTo(size);
                actualReadNotificationResponses
                        .forEach(
                                response -> softly.assertThat(response.createdAt()).isAfterOrEqualTo(limitStartDateTime)
                        );
                softly.assertThat(createdAts)
                        .isSortedAccordingTo(Comparator.reverseOrder());
            });
        }

        @DisplayName("조회된 데이터가 size와 동일할 경우 size만큼 불러와진다")
        @Test
        void success_returnsAllWhenDataSizeEqualsRequestSize() {
            // given
            List<Notification> notifications = List.of(
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 1))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 2))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 3))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 4))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 5))
                            .build()
            );
            notificationRepository.saveAll(notifications);

            int size = 5;
            ReadNotificationsRequest readNotificationsRequest = new ReadNotificationsRequest(6L, localDateTime, size);

            // when
            ReadNotificationsResponse readNotificationsResponse = notificationService.getNotificationsAfter(
                    readNotificationsRequest);

            // then
            assertThat(readNotificationsResponse.readNotificationResponses().size()).isEqualTo(size);
        }

        @DisplayName("요청 갯수보다 작아도 예외 없이 데이터가 불러와진다")
        @Test
        void success_returnsLessDataWhenAvailableDataIsLessThanSize() {
            // given
            List<Notification> notifications = List.of(
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 1))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 2))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 3))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 4))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 5))
                            .build()
            );
            notificationRepository.saveAll(notifications);

            int size = 10;
            ReadNotificationsRequest readNotificationsRequest = new ReadNotificationsRequest(6L, localDateTime, size);

            // when
            ReadNotificationsResponse readNotificationsResponse = notificationService.getNotificationsAfter(
                    readNotificationsRequest);

            // then
            assertThat(readNotificationsResponse.readNotificationResponses().size()).isEqualTo(notifications.size());
        }

        @DisplayName("lastId가 null이 들어오면 맨 마지막부터 불러와진다")
        @Test
        void success_returnsFromLatestWhenLastIdIsNull() {
            // given
            Notification lastestNotification = NotificationFixtureBuilder
                    .withMember(savedMember)
                    .createdAt(LocalDate.of(2025, 8, 10))
                    .build();

            List<Notification> notifications = List.of(
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 1))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 2))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 3))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 4))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 5))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 6))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 7))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 8))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 9))
                            .build(),
                    lastestNotification
            );
            notificationRepository.saveAll(notifications);

            int size = 5;
            ReadNotificationsRequest readNotificationsRequest = new ReadNotificationsRequest(null, localDateTime, size);

            // when
            ReadNotificationsResponse readNotificationsResponse = notificationService.getNotificationsAfter(
                    readNotificationsRequest);

            // then
            List<ReadNotificationResponse> readNotificationResponses = readNotificationsResponse.readNotificationResponses();

            assertSoftly(softly -> {
                softly.assertThat(readNotificationResponses.size()).isEqualTo(size);
                softly.assertThat(readNotificationResponses.getFirst().createdAt())
                        .isEqualTo(lastestNotification.getCreatedAt());
            });
        }

        @DisplayName("다음 데이터가 없을 경우 nextCursor은 null이 반환된다")
        @Test
        void success_nextCursorIsNullWhenNoMoreData() {
            // given
            List<Notification> notifications = List.of(
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 1))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 2))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 3))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 4))
                            .build(),
                    NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 5))
                            .build()
            );
            notificationRepository.saveAll(notifications);

            int size = 5;
            ReadNotificationsRequest readNotificationsRequest = new ReadNotificationsRequest(6L, localDateTime, size);

            // when
            ReadNotificationsResponse readNotificationsResponse = notificationService.getNotificationsAfter(
                    readNotificationsRequest);

            // then
            assertThat(readNotificationsResponse.nextCursor()).isNull();
        }

        @DisplayName("size가 음수일 때 예외가 발생한다")
        @Test
        void error_throwsExceptionWhenSizeIsNegative() {
            // given
            LocalDateTime localDateTime = LocalDateTime.of(2025, 8, 6, 10, 10);
            int size = -1;
            ReadNotificationsRequest readNotificationsRequest = new ReadNotificationsRequest(6L, localDateTime, size);

            // when & then
            assertThatThrownBy(
                    () -> notificationService.getNotificationsAfter(readNotificationsRequest)
            ).isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_PAGE_SIZE_RANGE.name());
        }
    }
}
