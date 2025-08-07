package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.ReadNotificationResponse;
import backend.mulkkam.notification.dto.GetNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.support.NotificationFixtureBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NotificationServiceUnitTest {

    private static final int DAY_LIMIT = 7;
    private Member mockMember;
    private final Long mockMemberId = 1L;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    NotificationService notificationService;

    @BeforeEach
    void setUP() {
        mockMember = mock(Member.class);
        given(memberRepository.findById(mockMemberId)).willReturn(Optional.of(mockMember));
    }

    @DisplayName("알림 조회 기능을 사용할 때")
    @Nested
    class GetNotificationsAfter {

        private final LocalDateTime requestTime = LocalDateTime.of(2025, 8, 7, 10, 10);
        private final LocalDateTime limitStartDateTime = requestTime.minusDays(DAY_LIMIT);
        private final int defaultSize = 5;

        private List<Notification> createNotifications(LocalDate... dates) {
            return Arrays.stream(dates)
                    .map(date -> NotificationFixtureBuilder.withMember(mockMember).createdAt(date).build())
                    .toList();
        }

        @DisplayName("요청 날짜로부터 7일 내의 최신순으로 데이터만이 불러와진다")
        @Test
        void success_returnsNotificationsWithin7DaysSortedByLatest() {
            // given
            Long lastId = 10L;
            GetNotificationsRequest request = new GetNotificationsRequest(lastId, requestTime, defaultSize);

            when(notificationRepository.findByCursor(mockMember, lastId, limitStartDateTime,
                    Pageable.ofSize(defaultSize + 1)))
                    .thenReturn(createNotifications(
                            LocalDate.of(2025, 8, 9),
                            LocalDate.of(2025, 8, 8),
                            LocalDate.of(2025, 8, 7),
                            LocalDate.of(2025, 8, 6),
                            LocalDate.of(2025, 8, 5)
                    ));

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, mockMemberId);

            // then
            List<ReadNotificationResponse> results = response.readNotificationResponses();
            List<LocalDateTime> createdAts = results.stream()
                    .map(ReadNotificationResponse::createdAt)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(results).hasSize(defaultSize);
                results.forEach(r -> softly.assertThat(r.createdAt()).isAfterOrEqualTo(limitStartDateTime));
                softly.assertThat(createdAts).isSortedAccordingTo(Comparator.reverseOrder());
            });
        }

        @DisplayName("조회된 데이터가 size와 동일할 경우 size만큼 불러와진다")
        @Test
        void success_returnsAllWhenDataSizeEqualsRequestSize() {
            // given
            Long lastId = 6L;
            when(notificationRepository.findByCursor(mockMember, lastId, limitStartDateTime, Pageable.ofSize(defaultSize + 1)))
                    .thenReturn(createNotifications(
                            LocalDate.of(2025, 8, 9),
                            LocalDate.of(2025, 8, 8),
                            LocalDate.of(2025, 8, 7),
                            LocalDate.of(2025, 8, 6),
                            LocalDate.of(2025, 8, 5)
                    ));

            GetNotificationsRequest request = new GetNotificationsRequest(lastId, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, mockMemberId);

            // then
            AssertionsForClassTypes.assertThat(response.readNotificationResponses().size()).isEqualTo(defaultSize);
        }

        @DisplayName("요청 갯수보다 작아도 예외 없이 데이터가 불러와진다")
        @Test
        void success_returnsLessDataWhenAvailableDataIsLessThanSize() {
            // given
            Long lastId = 6L;
            List<Notification> notifications = createNotifications(
                            LocalDate.of(2025, 8, 9),
                            LocalDate.of(2025, 8, 8),
                            LocalDate.of(2025, 8, 7),
                            LocalDate.of(2025, 8, 6),
                            LocalDate.of(2025, 8, 5)
                    );
            when(notificationRepository.findByCursor(mockMember, lastId, limitStartDateTime, Pageable.ofSize(10 + 1)))
                    .thenReturn(notifications);

            GetNotificationsRequest request = new GetNotificationsRequest(lastId, requestTime, 10);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, mockMemberId);

            // then
            AssertionsForClassTypes.assertThat(response.readNotificationResponses().size())
                    .isEqualTo(notifications.size());
        }

        @DisplayName("lastId가 null이 들어오면 맨 마지막부터 불러와진다")
        @Test
        void success_returnsFromLatestWhenLastIdIsNull() {
            // given
            Notification latestNotification = NotificationFixtureBuilder
                    .withMember(mockMember)
                    .createdAt(LocalDate.of(2025, 8, 9))
                    .build();

            List<Notification> notifications = createNotifications(
                    LocalDate.of(2025, 8, 9),
                    LocalDate.of(2025, 8, 8),
                    LocalDate.of(2025, 8, 7),
                    LocalDate.of(2025, 8, 6),
                    LocalDate.of(2025, 8, 5)
            );

            when(notificationRepository.findLatest(mockMember, limitStartDateTime, Pageable.ofSize(defaultSize + 1)))
                    .thenReturn(notifications);

            GetNotificationsRequest request = new GetNotificationsRequest(null, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request, mockMemberId);

            // then
            List<ReadNotificationResponse> readNotificationResponses = response.readNotificationResponses();

            assertSoftly(softly -> {
                softly.assertThat(readNotificationResponses.size()).isEqualTo(defaultSize);
                softly.assertThat(readNotificationResponses.getFirst().createdAt())
                        .isEqualTo(latestNotification.getCreatedAt());
            });
        }

        @DisplayName("size가 음수일 때 예외가 발생한다")
        @Test
        void error_throwsExceptionWhenSizeIsNegative() {
            // given
            GetNotificationsRequest request = new GetNotificationsRequest(6L, requestTime, -1);

            // when & then
            AssertionsForClassTypes.assertThatThrownBy(
                            () -> notificationService.getNotificationsAfter(request, mockMemberId))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_PAGE_SIZE_RANGE.name());
        }
    }
}
