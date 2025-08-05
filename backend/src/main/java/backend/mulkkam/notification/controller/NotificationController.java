package backend.mulkkam.notification.controller;

import backend.mulkkam.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    ResponseEntity<Void> get() {
        return null;
    }
}
