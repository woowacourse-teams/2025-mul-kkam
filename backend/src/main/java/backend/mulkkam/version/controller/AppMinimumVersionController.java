package backend.mulkkam.version.controller;

import backend.mulkkam.version.dto.AppMinimumVersionResponse;
import backend.mulkkam.version.service.AppMinimumVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "앱 버전", description = "앱 버전 정보 관련 API")
@RequiredArgsConstructor
@RequestMapping("/versions")
@RestController
public class AppMinimumVersionController {

    private final AppMinimumVersionService appMinimumVersionService;

    @Operation(summary = "앱 버전 확인", description = "최소한으로 등록되어 있어야 하는 앱의 버전을 확인합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @PostMapping
    public ResponseEntity<AppMinimumVersionResponse> read() {
        AppMinimumVersionResponse appMinimumVersionResponse = appMinimumVersionService.read();
        return ResponseEntity.ok().body(appMinimumVersionResponse);
    }
}
