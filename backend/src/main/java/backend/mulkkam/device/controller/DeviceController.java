package backend.mulkkam.device.controller;

import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.device.dto.RegisterDeviceRequest;
import backend.mulkkam.device.service.DeviceService;
import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
            Member member,
            @RequestBody RegisterDeviceRequest registerDeviceRequest) {
        deviceService.register(registerDeviceRequest, member);
        return ResponseEntity.ok().build();
    }
}
