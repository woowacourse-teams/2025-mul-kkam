package backend.mulkkam.support.fixture;

import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.domain.OutboxNotification.Status;
import java.time.LocalDateTime;

public class OutboxNotificationFixtureBuilder {

    private String type = "REMIND";
    private Long memberId = 1L;
    private String token = "test-fcm-token";
    private String title = "물 마실 시간이에요";
    private String body = "오늘도 건강한 하루 되세요!";
    private Status status = Status.READY;
    private String dedupeKey = "REMIND:1:" + LocalDateTime.now() + ":" + token;
    private int attemptCount = 0;
    private String lastError = null;
    private LocalDateTime nextAttemptAt = null;

    private OutboxNotificationFixtureBuilder() {
    }

    public static OutboxNotificationFixtureBuilder builder() {
        return new OutboxNotificationFixtureBuilder();
    }

    public OutboxNotificationFixtureBuilder type(String type) {
        this.type = type;
        return this;
    }

    public OutboxNotificationFixtureBuilder memberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public OutboxNotificationFixtureBuilder token(String token) {
        this.token = token;
        return this;
    }

    public OutboxNotificationFixtureBuilder title(String title) {
        this.title = title;
        return this;
    }

    public OutboxNotificationFixtureBuilder body(String body) {
        this.body = body;
        return this;
    }

    public OutboxNotificationFixtureBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public OutboxNotificationFixtureBuilder dedupeKey(String dedupeKey) {
        this.dedupeKey = dedupeKey;
        return this;
    }

    public OutboxNotificationFixtureBuilder attemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
        return this;
    }

    public OutboxNotificationFixtureBuilder lastError(String lastError) {
        this.lastError = lastError;
        return this;
    }

    public OutboxNotificationFixtureBuilder nextAttemptAt(LocalDateTime nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
        return this;
    }

    public OutboxNotification build() {
        return new OutboxNotification(
                type,
                memberId,
                token,
                title,
                body,
                status,
                dedupeKey,
                attemptCount,
                lastError,
                nextAttemptAt
        );
    }
}
