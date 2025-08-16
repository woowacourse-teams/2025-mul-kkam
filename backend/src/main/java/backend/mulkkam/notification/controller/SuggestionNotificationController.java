package backend.mulkkam.notification.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// TODO 2025. 8. 16. 19:17: swagger 설정 추가
@RequiredArgsConstructor
@RestController
@RequestMapping("/suggestion-notifications")
public class SuggestionNotificationController {

    private final SuggestionNotificationService suggestionNotificationService;

    @PostMapping("/approval/{id}")
    public ResponseEntity<Void> applyTargetAmount(
            @PathVariable
            Long id,
            @Parameter(hidden = true)
            MemberDetails memberDetails
    ) {
        suggestionNotificationService.applyTargetAmount(id, memberDetails);
        return ResponseEntity.ok().build();
    }
}
