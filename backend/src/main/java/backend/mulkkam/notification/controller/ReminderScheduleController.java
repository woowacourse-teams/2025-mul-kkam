package backend.mulkkam.notification.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.member.dto.request.ModifyIsReminderEnabledRequest;
import backend.mulkkam.notification.dto.request.CreateReminderScheduleRequest;
import backend.mulkkam.notification.dto.response.ReminderSchedulesResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리마인드 알림 설정", description = "사용자 리마인드 알림 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/reminder")
public class ReminderScheduleController {

    @PostMapping()
    public ResponseEntity<Void> create(
            CreateReminderScheduleRequest createReminderScheduleRequest,
            MemberDetails memberDetails
    ) {
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<ReminderSchedulesResponse> read(MemberDetails memberDetails) {
        List<LocalTime> times = List.of(
                LocalTime.of(10, 10),
                LocalTime.of(12, 0),
                LocalTime.of(15, 30),
                LocalTime.of(19, 0)
        );
        return ResponseEntity.ok().body(new ReminderSchedulesResponse(true, times));
    }

    @PatchMapping()
    public ResponseEntity<Void> modifyTime(
            ModifyIsReminderEnabledRequest modifyReminderScheduleTimeRequest,
            MemberDetails memberDetails
    ) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            MemberDetails memberDetails
    ) {
        return ResponseEntity.noContent().build();
    }
}
