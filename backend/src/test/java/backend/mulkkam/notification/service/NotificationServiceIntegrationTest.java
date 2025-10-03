package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.ReadNotificationRow;
import backend.mulkkam.notification.dto.ReadNotificationsRequest;
import backend.mulkkam.notification.dto.GetUnreadNotificationsCountResponse;
import backend.mulkkam.notification.dto.NotificationResponse;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.fixture.notification.NotificationFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import backend.mulkkam.support.fixture.notification.SuggestionNotificationFixtureBuilder;
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
import org.springframework.data.domain.Pageable;

class NotificationServiceIntegrationTest extends ServiceIntegrationTest {

    private static final int DAY_LIMIT = 7;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private Member savedMember;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SuggestionNotificationRepository suggestionNotificationRepository;

    private List<Notification> createUnReadNotifications(LocalDate... dates) {
        return Arrays.stream(dates)
                .map(date -> NotificationFixtureBuilder
                        .withMember(savedMember)
                        .createdAt(date)
                        .isRead(false)
                        .build())
                .toList();
    }

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder.builder().build();
        savedMember = memberRepository.save(member);
    }

    @DisplayName("알림 조회 기능을 사용할 때")
    @Nested
    class GetNotificationsAfter {

        private final LocalDateTime requestTime = LocalDateTime.of(2025, 8, 7, 10, 10);
        private final int defaultSize = 5;

        @DisplayName("요청 날짜로부터 7일 내의 최신순으로 데이터만이 불러와진다")
        @Test
        void success_returnsNotificationsWithin7DaysSortedByLatest() {
            // given
            notificationRepository.saveAll(List.of(
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 1))
                            .notificationType(NotificationType.NOTICE)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 2))
                            .notificationType(NotificationType.NOTICE)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 3))
                            .notificationType(NotificationType.REMIND)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 4))
                            .notificationType(NotificationType.REMIND)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 5))
                            .notificationType(NotificationType.REMIND)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 6))
                            .notificationType(NotificationType.REMIND)
                            .build()
            ));

            Notification notification1 = NotificationFixtureBuilder.withMember(savedMember)
                    .notificationType(NotificationType.SUGGESTION)
                    .createdAt(LocalDate.of(2025, 8, 7))
                    .build();
            Notification notification2 = NotificationFixtureBuilder.withMember(savedMember)
                    .notificationType(NotificationType.SUGGESTION)
                    .createdAt(LocalDate.of(2025, 8, 8))
                    .build();
            Notification notification3 = NotificationFixtureBuilder.withMember(savedMember)
                    .notificationType(NotificationType.SUGGESTION)
                    .createdAt(LocalDate.of(2025, 8, 9))
                    .build();
            Notification notification4 = NotificationFixtureBuilder.withMember(savedMember)
                    .notificationType(NotificationType.SUGGESTION)
                    .createdAt(LocalDate.of(2025, 8, 10))
                    .build();

            suggestionNotificationRepository.saveAll(List.of(
                    SuggestionNotificationFixtureBuilder.withNotification(notification1).build(),
                    SuggestionNotificationFixtureBuilder.withNotification(notification2).build(),
                    SuggestionNotificationFixtureBuilder.withNotification(notification3).build(),
                    SuggestionNotificationFixtureBuilder.withNotification(notification4).build()
            ));

            LocalDateTime limitStartDateTime = requestTime.minusDays(DAY_LIMIT);
            ReadNotificationsRequest request = new ReadNotificationsRequest(10L, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.readNotificationsAfter(request,
                    new MemberDetails(savedMember));

            // then
            List<NotificationResponse> results = response.readNotificationResponses();
            List<LocalDateTime> createdAts = results.stream().map(NotificationResponse::createdAt).toList();

            List<Boolean> actualIsReads = notificationRepository.findByCursorRows(savedMember.getId(), 10L,
                            requestTime, Pageable.ofSize(defaultSize + 1)).stream()
                    .map(ReadNotificationRow::isRead)
                            .toList();

            assertSoftly(softly -> {
                softly.assertThat(results).hasSize(defaultSize);
                results.forEach(r -> softly.assertThat(r.createdAt()).isAfterOrEqualTo(limitStartDateTime));
                results.forEach(r -> softly.assertThat(r.isRead()).isFalse());
                results.forEach(r -> softly.assertThat(r.id() < 10L).isTrue());
                softly.assertThat(createdAts).isSortedAccordingTo(Comparator.reverseOrder());
                softly.assertThat(actualIsReads).allMatch(o -> o == Boolean.TRUE);
                softly.assertThat(response.nextCursor()).isEqualTo(5);
            });
        }

        @DisplayName("조회된 데이터의 크기가 size와 동일할 경우 size만큼 불러와진다")
        @Test
        void success_returnsAllWhenDataSizeEqualsRequestSize() {
            // given
            notificationRepository.saveAll(createUnReadNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5)
            ));

            ReadNotificationsRequest request = new ReadNotificationsRequest(6L, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.readNotificationsAfter(request,
                    new MemberDetails(savedMember));

            // then
            assertThat(response.readNotificationResponses().size()).isEqualTo(defaultSize);
        }

        @DisplayName("요청 갯수보다 작아도 예외 없이 데이터가 불러와진다")
        @Test
        void success_returnsLessDataWhenAvailableDataIsLessThanSize() {
            // given
            List<Notification> notifications = createUnReadNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5)
            );
            notificationRepository.saveAll(notifications);

            ReadNotificationsRequest request = new ReadNotificationsRequest(6L, requestTime, 10);

            // when
            ReadNotificationsResponse response = notificationService.readNotificationsAfter(request,
                    new MemberDetails(savedMember));

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

            List<Notification> notifications = createUnReadNotifications(
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

            ReadNotificationsRequest request = new ReadNotificationsRequest(null, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.readNotificationsAfter(request,
                    new MemberDetails(savedMember));

            // then
            List<NotificationResponse> readNotificationResponses = response.readNotificationResponses();

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
            notificationRepository.saveAll(createUnReadNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5)
            ));

            ReadNotificationsRequest request = new ReadNotificationsRequest(6L, requestTime, defaultSize);

            // when
            ReadNotificationsResponse response = notificationService.readNotificationsAfter(request,
                    new MemberDetails(savedMember));

            // then
            assertThat(response.nextCursor()).isNull();
        }

        @DisplayName("size가 음수일 때 예외가 발생한다")
        @Test
        void error_throwsExceptionWhenSizeIsNegative() {
            // given
            ReadNotificationsRequest request = new ReadNotificationsRequest(6L, requestTime, -1);

            // when & then
            assertThatThrownBy(() -> notificationService.readNotificationsAfter(request, new MemberDetails(savedMember)))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_PAGE_SIZE_RANGE.name());
        }
    }

    @DisplayName("알림 개수를 조회하고자 할 때")
    @Nested
    class GetNotificationsCount {

        private LocalDateTime clientTime;
        @BeforeEach
        void setUp() {
            clientTime = LocalDateTime.of(2025, 8, 7, 0, 0, 0);

            // 공통 데이터 시드: 읽음 처리된 제안 알림 1건 + 8/1~8/5 읽음 + 8/6~8/7 미읽음
            Notification suggestionRead = NotificationFixtureBuilder.withMember(savedMember)
                    .notificationType(NotificationType.SUGGESTION)
                    .createdAt(LocalDate.of(2025, 8, 1))
                    .isRead(true)
                    .build();
            suggestionNotificationRepository.save(
                    SuggestionNotificationFixtureBuilder.withNotification(suggestionRead).build());

            notificationRepository.saveAll(createReadNotifications(
                    LocalDate.of(2025, 8, 1),
                    LocalDate.of(2025, 8, 2),
                    LocalDate.of(2025, 8, 3),
                    LocalDate.of(2025, 8, 4),
                    LocalDate.of(2025, 8, 5)
            ));
            notificationRepository.saveAll(createUnReadNotifications(
                    LocalDate.of(2025, 8, 6),
                    LocalDate.of(2025, 8, 7)
            ));
        }

        private List<Notification> createReadNotifications(LocalDate... dates) {
            return Arrays.stream(dates)
                    .map(date -> NotificationFixtureBuilder
                            .withMember(savedMember)
                            .createdAt(date)
                            .isRead(true)
                            .build())
                    .toList();
        }

        @DisplayName("안 읽은 알림의 갯수를 반환한다")
        @Test
        void success_validMember() {
            // when
            GetUnreadNotificationsCountResponse getUnreadNotificationsCountResponse = notificationService.getUnReadNotificationsCount(
                    new MemberDetails(savedMember), clientTime);

            // then
            assertThat(getUnreadNotificationsCountResponse.count()).isEqualTo(2);
        }

        @DisplayName("요청 시각 기준 7일 이내의 읽지 않은 알림만 집계한다")
        @Test
        void success_countsUnreadOnlyWithin7Days() {
            // given
            notificationRepository.saveAll(createUnReadNotifications(
                    LocalDate.of(2025, 7, 6),
                    LocalDate.of(2025, 7, 7)
            ));

            // when 
            GetUnreadNotificationsCountResponse getUnreadNotificationsCountResponse = notificationService.getUnReadNotificationsCount(
                    new MemberDetails(savedMember), clientTime);

            // then
            assertThat(getUnreadNotificationsCountResponse.count()).isEqualTo(2);
        }
    }

    @DisplayName("알림을 삭제할 때")
    @Nested
    class Delete {

        @DisplayName("존재하는 id 를 통해 삭제하는 경우 성공한다")
        @Test
        void success_existingId() {
            // given
            Notification notification = NotificationFixtureBuilder.withMember(savedMember)
                    .notificationType(NotificationType.REMIND)
                    .createdAt(LocalDate.of(2025, 8, 1))
                    .isRead(true)
                    .build();
            notificationRepository.save(notification);

            MemberDetails memberDetails = new MemberDetails(savedMember.getId());

            // when
            notificationService.delete(memberDetails, notification.getId());

            // then
            assertThat(notificationRepository.findById(notification.getId())).isEmpty();
        }

        @DisplayName("타입이 suggestion 인 경우 SuggestionNotification 까지 삭제된다")
        @Test
        void success_typeIsSuggestion() {
            // given
            Notification notification = NotificationFixtureBuilder.withMember(savedMember)
                    .notificationType(NotificationType.SUGGESTION)
                    .createdAt(LocalDate.of(2025, 8, 1))
                    .isRead(true)
                    .build();
            suggestionNotificationRepository.save(
                    SuggestionNotificationFixtureBuilder.withNotification(notification).build());
            notificationRepository.save(notification);

            MemberDetails memberDetails = new MemberDetails(savedMember.getId());

            // when
            notificationService.delete(memberDetails, notification.getId());

            // then
            assertSoftly(softly -> {
                assertThat(notificationRepository.findById(notification.getId())).isEmpty();
                assertThat(suggestionNotificationRepository.findById(notification.getId())).isEmpty();
            });
        }

        @DisplayName("존재하지 않는 id 인 경우 예외를 던진다")
        @Test
        void success_notExistingId() {
            // given
            MemberDetails memberDetails = new MemberDetails(savedMember.getId());

            // when & then
            assertThatThrownBy(() -> notificationService.delete(memberDetails, 1L))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(NotFoundErrorCode.NOT_FOUND_NOTIFICATION.name());
        }

        @DisplayName("삭제할 권한이 없는 경우 예외를 던진다")
        @Test
        void success_unauthorizedToDelete() {
            // given
            Notification notification = NotificationFixtureBuilder.withMember(savedMember)
                    .notificationType(NotificationType.SUGGESTION)
                    .createdAt(LocalDate.of(2025, 8, 1))
                    .isRead(true)
                    .build();
            suggestionNotificationRepository.save(
                    SuggestionNotificationFixtureBuilder.withNotification(notification).build());
            notificationRepository.save(notification);

            Member anotherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            memberRepository.save(anotherMember);

            MemberDetails memberDetails = new MemberDetails(anotherMember.getId());

            // when & then
            assertThatThrownBy(() -> notificationService.delete(memberDetails, 1L))
                    .isInstanceOf(CommonException.class)
                    .hasMessageContaining(ForbiddenErrorCode.NOT_PERMITTED_FOR_NOTIFICATION.name());
        }
    }
}
