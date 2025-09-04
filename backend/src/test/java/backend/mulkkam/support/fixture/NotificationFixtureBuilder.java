package backend.mulkkam.support.fixture;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NotificationFixtureBuilder {

    private final Member member;
    private NotificationType notificationType = NotificationType.NOTICE;
    private String title = "notificationTitle";
    private boolean isRead = false;
    private LocalTime localTime = LocalTime.of(10, 30);
    private LocalDateTime createdAt = LocalDateTime.of(LocalDate.now(), localTime);

    private NotificationFixtureBuilder(Member member) {
        this.member = member;
    }

    public static NotificationFixtureBuilder withMember(Member member) {
        return new NotificationFixtureBuilder(member);
    }

    public NotificationFixtureBuilder notificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public NotificationFixtureBuilder title(String title) {
        this.title = title;
        return this;
    }

    public NotificationFixtureBuilder isRead(boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    public NotificationFixtureBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public NotificationFixtureBuilder createdAt(LocalDate localDate) {
        this.createdAt = LocalDateTime.of(localDate, localTime);
        return this;
    }

    public Notification build() {
        return new Notification(
                notificationType,
                title,
                isRead,
                createdAt,
                member
        );
    }
}
