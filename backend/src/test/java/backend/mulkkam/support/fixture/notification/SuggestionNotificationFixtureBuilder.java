package backend.mulkkam.support.fixture.notification;

import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;

public class SuggestionNotificationFixtureBuilder {

    private final Notification notification;
    private int recommendedTargetAmount = 1_800;
    private boolean applyTargetAmount = false;

    private SuggestionNotificationFixtureBuilder(Notification notification) {
        this.notification = notification;
    }

    public static SuggestionNotificationFixtureBuilder withNotification(Notification notification) {
        return new SuggestionNotificationFixtureBuilder(notification);
    }

    public SuggestionNotificationFixtureBuilder recommendedTargetAmount(int value) {
        this.recommendedTargetAmount = value;
        return this;
    }

    public SuggestionNotificationFixtureBuilder applyTargetAmount(boolean applyTargetAmount) {
        this.applyTargetAmount = applyTargetAmount;
        return this;
    }

    public SuggestionNotification build() {
        return new SuggestionNotification(
                recommendedTargetAmount,
                applyTargetAmount,
                notification
        );
    }
}
