package backend.mulkkam.notification.dto.request;

public record MaintenanceNotificationRequest(
        String title,
        String body,
        String secretKey
) {
}
