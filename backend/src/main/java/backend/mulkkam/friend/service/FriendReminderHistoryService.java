package backend.mulkkam.friend.service;

import backend.mulkkam.averageTemperature.domain.City;
import backend.mulkkam.averageTemperature.domain.CityDateTime;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.domain.FriendReminderHistory;
import backend.mulkkam.friend.dto.request.CreateFriendReminderRequest;
import backend.mulkkam.friend.service.command.FriendReminderHistoryCommandService;
import backend.mulkkam.friend.service.query.FriendQueryService;
import backend.mulkkam.friend.service.query.FriendReminderHistoryQueryService;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.service.MemberQueryService;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.service.RemindNotificationMessageTemplateProvider;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@AllArgsConstructor
@Service
public class FriendReminderHistoryService {

    private final MemberQueryService memberQueryService;
    private final FriendQueryService friendQueryService;
    private final FriendReminderHistoryQueryService friendReminderHistoryQueryService;
    private final FriendReminderHistoryCommandService friendReminderHistoryCommandService;

    private final SuggestionNotificationService suggestionNotificationService;

    @Transactional
    public void createAndSendReminder(
            CreateFriendReminderRequest request,
            MemberDetails memberDetails
    ) {
        Long senderId = memberDetails.id();
        Long friendId = request.memberId();
        friendQueryService.validateFriends(friendId, memberDetails.id());

        LocalDate today = CityDateTime.now(City.SEOUL).getLocalDate();
        FriendReminderHistory reminderHistory = friendReminderHistoryQueryService.getOrCreateDefault(
                senderId, friendId, today
        );

        friendReminderHistoryCommandService.reduceRemainingCount(reminderHistory.getId());

        sendNotification(senderId, friendId);
    }

    private void sendNotification(Long senderId, Long friendId) {
        MemberNickname senderName = memberQueryService.getNickname(senderId);
        NotificationMessageTemplate template =
                RemindNotificationMessageTemplateProvider.getFriendReminderMessageTemplate(senderName);
        suggestionNotificationService.createAndSendNotification(template, friendId);
    }
}
