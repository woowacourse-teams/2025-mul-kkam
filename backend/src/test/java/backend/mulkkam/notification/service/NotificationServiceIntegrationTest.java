package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.ReadNotificationResponse;
import backend.mulkkam.notification.dto.GetNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.NotificationFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationServiceIntegrationTest extends ServiceIntegrationTest {

    private static final int DAY_LIMIT = 7;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private Member savedMember;
    private Long savedMemberId;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder.builder().build();
        savedMember = memberRepository.save(member);
        savedMemberId = savedMember.getId();
    }

    @DisplayName("알림 조회 기능을 사용할 때")
    @Nested
    class GetNotificationsAfter {

        private final LocalDateTime requestTime = LocalDateTime.of(2025, 8, 7, 10, 10);
        private final int defaultSize = 5;

        private List<Notification> createNotifications(LocalDate... dates) {
            return Arrays.stream(dates)
                    .map(date -> NotificationFixtureBuilder.withMember(savedMember).createdAt(date).build())
                    .toList();
        }

        @DisplayName("요청 날짜로부터 7일 내의 최신순으로 데이터만이 불러와진다")
        @Test
        void success_returnsNotificationsWithin7DaysSortedByLatest() {
            // given
            notificationRepository.saveAll(createNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5),
                    LocalDate.of(2025, 8, 6),
                    LocalDate.of(2025, 8, 7),
                    LocalDate.of(2025, 8, 8),
                    LocalDate.of(2025, 8, 9),
                    LocalDate.of(2025, 8, 10)
            ));

            LocalDateTime limitStartDateTime = requestTime.minusDays(DAY_LIMIT);
            GetNotificationsRequest request = new GetNotificationsRequest(10L, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, new MemberDetails(savedMember));

            // then
            List<ReadNotificationResponse> results = response.readNotificationResponses();
            List<LocalDateTime> createdAts = results.stream().map(ReadNotificationResponse::createdAt).toList();

            assertSoftly(softly -> {
                softly.assertThat(results).hasSize(defaultSize);
                results.forEach(r -> softly.assertThat(r.createdAt()).isAfterOrEqualTo(limitStartDateTime));
                softly.assertThat(createdAts).isSortedAccordingTo(Comparator.reverseOrder());
            });
        }

        @DisplayName("조회된 데이터의 크기가 size와 동일할 경우 size만큼 불러와진다")
        @Test
        void success_returnsAllWhenDataSizeEqualsRequestSize() {
            // given
            notificationRepository.saveAll(createNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5)
            ));

            GetNotificationsRequest request = new GetNotificationsRequest(6L, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, new MemberDetails(savedMember));

            // then
            assertThat(response.readNotificationResponses().size()).isEqualTo(defaultSize);
        }

        @DisplayName("요청 갯수보다 작아도 예외 없이 데이터가 불러와진다")
        @Test
        void success_returnsLessDataWhenAvailableDataIsLessThanSize() {
            // given
            List<Notification> notifications = createNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5)
            );
            notificationRepository.saveAll(notifications);

            GetNotificationsRequest request = new GetNotificationsRequest(6L, requestTime, 10);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, new MemberDetails(savedMember));

            // then
            assertThat(response.readNotificationResponses().size()).isEqualTo(notifications.size());
        }

        @DisplayName("lastId가 null이 들어오면 맨 마지막부터 불러와진다")
        @Test
        void success_returnsFromLatestWhenLastIdIsNull() {
            // given
            Notification latestNotification = NotificationFixtureBuilder
                    .withMember(savedMember)
                    .createdAt(LocalDate.of(2025, 8, 10))
                    .build();

            List<Notification> notifications = createNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5),
                    LocalDate.of(2025, 8, 6),
                    LocalDate.of(2025, 8, 7),
                    LocalDate.of(2025, 8, 8),
                    LocalDate.of(2025, 8, 9)
            );
            notificationRepository.saveAll(notifications);
            notificationRepository.save(latestNotification);

            GetNotificationsRequest request = new GetNotificationsRequest(null, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, new MemberDetails(savedMember));

            // then
            List<ReadNotificationResponse> readNotificationResponses = response.readNotificationResponses();

            assertSoftly(softly -> {
                softly.assertThat(readNotificationResponses.size()).isEqualTo(defaultSize);
                softly.assertThat(readNotificationResponses.getFirst().createdAt())
                        .isEqualTo(latestNotification.getCreatedAt());
            });
        }

        @DisplayName("다음 데이터가 없을 경우 nextCursor은 null이 반환된다")
        @Test
        void success_nextCursorIsNullWhenNoMoreData() {
            // given
            notificationRepository.saveAll(createNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5)
            ));

            GetNotificationsRequest request = new GetNotificationsRequest(6L, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, new MemberDetails(savedMember));

            // then
            assertThat(response.nextCursor()).isNull();
        }

        @DisplayName("size가 음수일 때 예외가 발생한다")
        @Test
        void error_throwsExceptionWhenSizeIsNegative() {
            // given
            GetNotificationsRequest request = new GetNotificationsRequest(6L, requestTime, -1);

            // when & then
            assertThatThrownBy(() -> notificationService.getNotificationsAfter(request, new MemberDetails(savedMember)))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_PAGE_SIZE_RANGE.name());
        }
    }
}
