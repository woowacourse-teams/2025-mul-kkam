package backend.mulkkam.notification.service;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import java.util.List;
import java.util.random.RandomGenerator;

public class RemindNotificationMessageTemplateProvider {

    private static final List<NotificationMessageTemplate> templates = List.of(
            remind("물 마실 시간!", "지금 이 순간 건강을 위해 물 한 잔 마셔보는 건 어떠세요?")
    );

    private static NotificationMessageTemplate remind(String title, String body) {
        return new NotificationMessageTemplate(title, body, Action.GO_HOME, NotificationType.REMIND);
    }

    public static NotificationMessageTemplate getRandomMessageTemplate() {
        int randomIdx = RandomGenerator.getDefault().nextInt(0, templates.size());
        return templates.get(randomIdx);
    }

    public static NotificationMessageTemplate getFriendReminderMessageTemplate(MemberNickname senderNickname) {
        String sender = senderNickname.value();
        String title = String.format("%s 님의 물풍선 공격을 받았어요!", sender);
        String body = String.format("%s 님이 물을 마시라고 재촉하네요. 지금 물 한 잔 마셔보는 건 어떠세요?", sender);
        return new NotificationMessageTemplate(title, body, Action.FRIEND_REMINDER, NotificationType.REMIND);
    }
}
