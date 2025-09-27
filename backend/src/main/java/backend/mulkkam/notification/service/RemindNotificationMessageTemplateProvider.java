package backend.mulkkam.notification.service;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import java.util.List;
import java.util.random.RandomGenerator;

public class RemindNotificationMessageTemplateProvider {

    private static final String DEFAULT_TOPIC = "mulkkam";

    private static final List<NotificationMessageTemplate> templates = List.of(
            remind("물 마실 시간!", "지금 이 순간 건강을 위해 물 한 잔 마셔보는 건 어떠세요?")
    );

    private static NotificationMessageTemplate remind(String title, String body) {
        return new NotificationMessageTemplate(title, body, DEFAULT_TOPIC, Action.GO_HOME, NotificationType.REMIND);
    }

    public static NotificationMessageTemplate getRandomMessageTemplate() {
        int randomIdx = RandomGenerator.getDefault().nextInt(0, templates.size());
        return templates.get(randomIdx);
    }
}
