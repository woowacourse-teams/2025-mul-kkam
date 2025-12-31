package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.EXCEED_FRIEND_REMINDER_LIMIT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.NOT_ALLOWED_SELF_REMINDER;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.dto.request.CreateFriendReminderRequest;
import backend.mulkkam.friend.service.command.FriendReminderHistoryCommandService;
import backend.mulkkam.friend.service.query.FriendQueryService;
import backend.mulkkam.friend.service.query.FriendReminderHistoryQueryService;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.service.MemberQueryService;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class FriendReminderHistoryServiceUnitTest {
    @InjectMocks
    private FriendReminderHistoryService friendReminderHistoryService;

    @Mock
    private MemberQueryService memberQueryService;

    @Mock
    private FriendQueryService friendQueryService;

    @Mock
    private FriendReminderHistoryQueryService friendReminderHistoryQueryService;

    @Mock
    private FriendReminderHistoryCommandService friendReminderHistoryCommandService;

    @Mock
    private SuggestionNotificationService suggestionNotificationService;

    private MemberDetails memberDetails;
    private CreateFriendReminderRequest request;
    private Long senderId;
    private Long friendId;

    @BeforeEach
    void setUp() {
        senderId = 1L;
        friendId = 2L;
        memberDetails = new MemberDetails(senderId);
        request = new CreateFriendReminderRequest(friendId);
    }

    @DisplayName("친구에게 리마인더를 보낼 때")
    @Nested
    class CreateAndSendReminder {

        @DisplayName("정상적으로 리마인더를 생성하고 알림을 전송한다")
        @Test
        void success_whenValidFriend() {
            // given
            LocalDate now = LocalDate.now();
            MemberNickname senderNickname = new MemberNickname("테스터");

            when(memberQueryService.getNickname(senderId)).thenReturn(senderNickname);

            // when
            friendReminderHistoryService.createAndSendReminder(request, memberDetails);

            // then
            verify(friendQueryService).validateFriends(friendId, senderId);
            verify(friendReminderHistoryCommandService).consumeRemainingCount(senderId, friendId, now);
            verify(memberQueryService).getNickname(senderId);
            verify(suggestionNotificationService).createAndSendNotification(any(NotificationMessageTemplate.class), eq(friendId));
        }

        @DisplayName("친구 관계가 아닌 경우 예외가 발생한다")
        @Test
        void fail_whenNotFriends() {
            // given
            doThrow(new CommonException(NOT_FOUND_FRIEND))
                    .when(friendQueryService)
                    .validateFriends(friendId, senderId);

            // when & then
            assertThatThrownBy(() -> friendReminderHistoryService.createAndSendReminder(request, memberDetails))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_FRIEND.name());

            verify(friendQueryService).validateFriends(friendId, senderId);
            verify(friendReminderHistoryCommandService, never()).consumeRemainingCount(anyLong(), anyLong(), any(LocalDate.class));
            verify(suggestionNotificationService, never()).createAndSendNotification(any(), anyLong());
        }

        @DisplayName("일일 전송 횟수를 초과하면 예외가 발생한다")
        @Test
        void fail_whenExceedDailyLimit() {
            // given
            LocalDate now = LocalDate.now();

            doThrow(new CommonException(EXCEED_FRIEND_REMINDER_LIMIT))
                    .when(friendReminderHistoryCommandService)
                    .consumeRemainingCount(senderId, friendId, now);

            // when & then
            assertThatThrownBy(() -> friendReminderHistoryService.createAndSendReminder(request, memberDetails))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(EXCEED_FRIEND_REMINDER_LIMIT.name());

            verify(friendQueryService).validateFriends(friendId, senderId);
            verify(friendReminderHistoryCommandService).consumeRemainingCount(senderId, friendId, now);
            verify(suggestionNotificationService, never()).createAndSendNotification(any(), anyLong());
        }

        @DisplayName("자기 자신에게 리마인더를 보내려고 하면 예외가 발생한다")
        @Test
        void fail_whenSelfReminder() {
            // given
            CreateFriendReminderRequest selfRequest = new CreateFriendReminderRequest(senderId);

            // when & then
            assertThatThrownBy(() -> friendReminderHistoryService.createAndSendReminder(selfRequest, memberDetails))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_ALLOWED_SELF_REMINDER.name());
        }
    }
}
