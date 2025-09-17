package backend.mulkkam.notification.service;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import java.util.List;

public class NotificationMessageTemplateProvider {

    private static final List<NotificationMessageTemplate> templates = List.of(
            new NotificationMessageTemplate("물 마실 시간!", "지금 이 순간 건강을 위해 물 한 잔 마셔보는 건 어떠세요?", "mulkkam", Action.GO_HOME,
                    NotificationType.REMIND)
    );

    public static List<NotificationMessageTemplate> findByType(NotificationType type) {
        return templates.stream()
                .filter(t -> t.type() == type)
                .toList();
    }
}
