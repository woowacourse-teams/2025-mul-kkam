package backend.mulkkam.notification.controller;

import backend.mulkkam.notification.dto.ReadNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    ResponseEntity<ReadNotificationsResponse> getNotifications(@Valid @ModelAttribute ReadNotificationsRequest readNotificationsRequest) {
        ReadNotificationsResponse readNotificationsResponse = notificationService.getNotificationsAfter(readNotificationsRequest);
        return ResponseEntity.ok(readNotificationsResponse);
    }
}
