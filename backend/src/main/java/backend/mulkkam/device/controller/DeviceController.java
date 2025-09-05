package backend.mulkkam.device.controller;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.device.dto.RegisterDeviceRequest;
import backend.mulkkam.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "디바이스", description = "사용자의 디바이스 정보 관리 API")
@RequiredArgsConstructor
@RequestMapping("/devices")
@RestController
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "사용자의 디바이스 정보 등록", description = "사용자의 디바이스를 등록합니다. 주어진 디바이스에 이미 토큰 값이 할당되어 있는 경우, 새로운 토큰 값으로 덮어 씌웁니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class)))
    @PostMapping
    public ResponseEntity<Void> register(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestBody RegisterDeviceRequest registerDeviceRequest) {
        deviceService.register(registerDeviceRequest, memberDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현재 기기의 FCM 토큰 삭제", description = "기기의 FCM 토큰을 NULL로 설정")
    @ApiResponse(responseCode = "204", description = "삭제(무효화) 완료")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "404", description = "기기 없음(선택, 노출하지 않아도 됨)")
    @DeleteMapping("/fcm-token")
    public ResponseEntity<Void> deleteFcmToken(
            @Parameter(hidden = true)
            MemberDetails memberDetails,
            @RequestHeader("X-Device-Id")
            String deviceId) {
        deviceService.deleteFcmToken(deviceId, memberDetails);
        return ResponseEntity.ok().build();
    }
}
