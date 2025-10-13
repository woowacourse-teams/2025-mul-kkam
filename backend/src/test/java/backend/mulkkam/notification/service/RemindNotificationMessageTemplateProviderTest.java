package backend.mulkkam.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RemindNotificationMessageTemplateProviderTest {

    @DisplayName("랜덤 메시지 템플릿을 조회할 때")
    @Nested
    class GetRandomMessageTemplate {

        @DisplayName("리마인드 타입의 템플릿을 반환한다")
        @Test
        void success_returnRemindType() {
            // when
            NotificationMessageTemplate template = RemindNotificationMessageTemplateProvider.getRandomMessageTemplate();

            // then
            assertSoftly(softly -> {
                softly.assertThat(template.type()).isEqualTo(NotificationType.REMIND);
                softly.assertThat(template.action()).isEqualTo(Action.GO_HOME);
                softly.assertThat(template.title()).isNotBlank();
                softly.assertThat(template.body()).isNotBlank();
            });
        }

        @DisplayName("여러 번 호출해도 항상 유효한 템플릿을 반환한다")
        @Test
        void success_multipleCallsReturnValidTemplates() {
            // when
            Set<String> titles = new HashSet<>();
            for (int i = 0; i < 100; i++) {
                NotificationMessageTemplate template = RemindNotificationMessageTemplateProvider.getRandomMessageTemplate();
                titles.add(template.title());
                
                assertSoftly(softly -> {
                    softly.assertThat(template.type()).isEqualTo(NotificationType.REMIND);
                    softly.assertThat(template.action()).isEqualTo(Action.GO_HOME);
                    softly.assertThat(template.title()).isNotBlank();
                    softly.assertThat(template.body()).isNotBlank();
                });
            }

            // then - 최소한 하나 이상의 템플릿이 존재
            assertThat(titles).isNotEmpty();
        }

        @DisplayName("템플릿에는 제목과 본문이 모두 포함되어 있다")
        @Test
        void success_templateHasTitleAndBody() {
            // when
            NotificationMessageTemplate template = RemindNotificationMessageTemplateProvider.getRandomMessageTemplate();

            // then
            assertSoftly(softly -> {
                softly.assertThat(template.title()).isEqualTo("물 마실 시간!");
                softly.assertThat(template.body()).isEqualTo("지금 이 순간 건강을 위해 물 한 잔 마셔보는 건 어떠세요?");
            });
        }
    }
}