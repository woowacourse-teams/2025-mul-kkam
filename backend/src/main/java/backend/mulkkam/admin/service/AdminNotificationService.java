package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.response.AdminNotificationListResponse;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<AdminNotificationListResponse> getNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable)
                .map(AdminNotificationListResponse::from);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        notificationRepository.delete(notification);
    }
}
