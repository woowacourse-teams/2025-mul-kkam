package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.averageTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.service.FcmService;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.GetNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationResponse;
import backend.mulkkam.notification.dto.GetNotificationsCountResponse;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.NotificationFixtureBuilder;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class NotificationServiceUnitTest {

    private static final int DAY_LIMIT = 7;

    private final Long memberId = 1L;
    private final Member member = MemberFixtureBuilder.builder().buildWithId(memberId);

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    DeviceRepository deviceRepository;

    @Mock
    FcmService fcmService;

    @InjectMocks
    NotificationService notificationService;

    @DisplayName("알림 조회 기능을 사용할 때")
    @Nested
    class GetNotificationsAfter {

        private final LocalDateTime requestTime = LocalDateTime.of(2025, 8, 7, 10, 10);
        private final LocalDateTime limitStartDateTime = requestTime.minusDays(DAY_LIMIT);
        private final int defaultSize = 5;

        private List<Notification> createNotifications(LocalDate... dates) {
            return Arrays.stream(dates)
                    .map(date -> NotificationFixtureBuilder.withMember(member).createdAt(date).build())
                    .toList();
        }

        @DisplayName("요청 날짜로부터 7일 내의 최신순으로 데이터만이 불러와진다")
        @Test
        void success_returnsNotificationsWithin7DaysSortedByLatest() {
            // given
            Long lastId = 10L;
            GetNotificationsRequest request = new GetNotificationsRequest(lastId, requestTime, defaultSize);

            when(notificationRepository.findByCursor(memberId, lastId, limitStartDateTime,
                    Pageable.ofSize(defaultSize + 1)))
                    .thenReturn(createNotifications(
                            LocalDate.of(2025, 8, 9),
                            LocalDate.of(2025, 8, 8),
                            LocalDate.of(2025, 8, 7),
                            LocalDate.of(2025, 8, 6),
                            LocalDate.of(2025, 8, 5)
                    ));

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request,
                    new MemberDetails(member));

            // then
            List<ReadNotificationResponse> results = response.readNotificationResponses();
            List<LocalDateTime> createdAts = results.stream()
                    .map(ReadNotificationResponse::createdAt)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(results).hasSize(defaultSize);
                results.forEach(r -> softly.assertThat(r.createdAt()).isAfterOrEqualTo(limitStartDateTime));
                results.forEach(r -> softly.assertThat(r.isRead()).isTrue());
                softly.assertThat(createdAts).isSortedAccordingTo(Comparator.reverseOrder());
            });
        }

        @DisplayName("조회된 데이터가 size와 동일할 경우 size만큼 불러와진다")
        @Test
        void success_returnsAllWhenDataSizeEqualsRequestSize() {
            // given
            Long lastId = 6L;
            when(notificationRepository.findByCursor(memberId, lastId, limitStartDateTime,
                    Pageable.ofSize(defaultSize + 1)))
                    .thenReturn(createNotifications(
                            LocalDate.of(2025, 8, 9),
                            LocalDate.of(2025, 8, 8),
                            LocalDate.of(2025, 8, 7),
                            LocalDate.of(2025, 8, 6),
                            LocalDate.of(2025, 8, 5)
                    ));

            GetNotificationsRequest request = new GetNotificationsRequest(lastId, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request,
                    new MemberDetails(member));

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
            when(notificationRepository.findByCursor(memberId, lastId, limitStartDateTime, Pageable.ofSize(10 + 1)))
                    .thenReturn(notifications);

            GetNotificationsRequest request = new GetNotificationsRequest(lastId, requestTime, 10);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request,
                    new MemberDetails(member));

            // then
            AssertionsForClassTypes.assertThat(response.readNotificationResponses().size())
                    .isEqualTo(notifications.size());
        }

        @DisplayName("lastId가 null이 들어오면 맨 마지막부터 불러와진다")
        @Test
        void success_returnsFromLatestWhenLastIdIsNull() {
            // given
            Notification latestNotification = NotificationFixtureBuilder
                    .withMember(member)
                    .createdAt(LocalDate.of(2025, 8, 9))
                    .build();

            List<Notification> notifications = createNotifications(
                    LocalDate.of(2025, 8, 9),
                    LocalDate.of(2025, 8, 8),
                    LocalDate.of(2025, 8, 7),
                    LocalDate.of(2025, 8, 6),
                    LocalDate.of(2025, 8, 5)
            );

            when(notificationRepository.findLatest(memberId, limitStartDateTime, Pageable.ofSize(defaultSize + 1)))
                    .thenReturn(notifications);

            GetNotificationsRequest request = new GetNotificationsRequest(null, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.getNotificationsAfter(request,
                    new MemberDetails(member));

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
                            () -> notificationService.getNotificationsAfter(request, new MemberDetails(member)))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_PAGE_SIZE_RANGE.name());
        }
    }

    @DisplayName("토큰 방식의 알림을 생성 및 전송 할 때")
    @Nested
    class CreateAndSendTokenNotification {

        @DisplayName("올바른 데이터로 요청이 성공한다")
        @Test
        void success_validInput() {
            // given
            CreateTokenNotificationRequest createTokenNotificationRequest = new CreateTokenNotificationRequest(
                    "title",
                    "body",
                    member,
                    Action.GO_NOTIFICATION,
                    NotificationType.SUGGESTION,
                    new TargetAmount(1000),
                    LocalDateTime.of(2025, 1, 2, 3, 4)
            );

            Device d1 = mock(Device.class);
            when(d1.getToken()).thenReturn("token-1");
            when(deviceRepository.findAllByMember(member)).thenReturn(List.of(d1));

            when(notificationRepository.save(any(Notification.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationService.createAndSendTokenNotification(createTokenNotificationRequest);

            // then
            verify(notificationRepository).save(
                    argThat(notification ->
                            notification.getNotificationType() == NotificationType.SUGGESTION &&
                                    notification.getTitle().equals("body") &&
                                    notification.getCreatedAt().equals(LocalDateTime.of(2025, 1, 2, 3, 4)) &&
                                    notification.getRecommendedTargetAmount().equals(new TargetAmount(1000)) &&
                                    notification.getMember() == member
                    ));

            verify(deviceRepository).findAllByMember(member);

            verify(fcmService).sendMessageByToken(
                    argThat(o ->
                            o.title().equals("title") &&
                                    o.body().equals("body") &&
                                    o.token().equals("token-1") &&
                                    o.action() == Action.GO_NOTIFICATION
                    )
            );
        }
    }

    @DisplayName("알림 개수를 조회하고자 할 때")
    @Nested
    class GetNotificationsCount {

        @DisplayName("안 읽은 알림의 갯수를 반환한다")
        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 3L})
        void success_validMember(long count) {
            // given
            when(notificationRepository.countByIsReadFalseAndMemberId(any(Long.class))).thenReturn(count);

            // when
            GetNotificationsCountResponse getNotificationsCountResponse = notificationService.getNotificationsCount(
                    new MemberDetails(member));

            // then
            assertThat(getNotificationsCountResponse.count()).isEqualTo(count);
        }
    }
}
