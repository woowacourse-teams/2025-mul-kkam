package backend.mulkkam.notification.controller;

import backend.mulkkam.notification.dto.CreateActivityNotification;
import backend.mulkkam.notification.dto.GetNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    ResponseEntity<ReadNotificationsResponse> getNotifications(@Valid @ModelAttribute GetNotificationsRequest getNotificationsRequest) {
        ReadNotificationsResponse readNotificationsResponse = notificationService.getNotificationsAfter(
                getNotificationsRequest, 1L);
        return ResponseEntity.ok(readNotificationsResponse);
    }

    @PostMapping("/activity")
    ResponseEntity<Void> createNotificationByActivity(@RequestBody CreateActivityNotification createActivityNotification) {
        notificationService.createActivityNotification(createActivityNotification, 1L);
        return ResponseEntity.ok().build();
    }
}
