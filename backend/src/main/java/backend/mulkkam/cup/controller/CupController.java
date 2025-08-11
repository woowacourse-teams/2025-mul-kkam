package backend.mulkkam.cup.controller;

import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupsRanksResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.service.CupService;
import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "컵", description = "사용자 컵 관리 API")
@RequiredArgsConstructor
@RequestMapping("/cups")
@RestController
public class CupController {
    private final CupService cupService;

    @Operation(
            summary = "사용자의 컵 리스트 반환",
            description = "사용자가 생성한 커스텀 컵 리스트를 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공 응답",
            content = @Content(schema = @Schema(implementation = CupsResponse.class))
    )
    @GetMapping
    public ResponseEntity<CupsResponse> read(
            @Parameter(hidden = true)
            Member member
    ) {
        return ResponseEntity.ok().body(cupService.readCupsByMemberId(member));
    }

    @Operation(
            summary = "새로운 컵 생성",
            description = "사용자가 새로운 커스텀 컵을 생성합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "컵 생성 성공"
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = FailureBody.class))
    )
    @PostMapping
    public ResponseEntity<Void> create(
            @Parameter(hidden = true)
            Member member,
            @RequestBody CreateCupRequest registerCupRequest
    ) {
        cupService.create(
                registerCupRequest,
                member
        );
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "컵 순위 업데이트",
            description = "사용자의 컵 목록의 순위를 일괄 업데이트합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "순위 업데이트 성공",
            content = @Content(schema = @Schema(implementation = CupsRanksResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = FailureBody.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음",
            content = @Content(schema = @Schema(implementation = FailureBody.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "순위 값 중복 또는 컵 식별자 중복",
            content = @Content(schema = @Schema(implementation = FailureBody.class))
    )
    @PutMapping("/ranks")
    public ResponseEntity<CupsRanksResponse> updateRanks(
            @Parameter(hidden = true)
            Member member,
            @RequestBody UpdateCupRanksRequest request
    ) {
        CupsRanksResponse response = cupService.updateRanks(request, member);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "컵 정보 수정",
            description = "특정 컵의 닉네임과 용량을 수정합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "컵 정보 수정 성공"
    )
    @ApiResponse(
            responseCode = "404",
            description = "컵을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = FailureBody.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음",
            content = @Content(schema = @Schema(implementation = FailureBody.class))
    )
    @PatchMapping("/{cupId}")
    public ResponseEntity<Void> modifyNicknameAndAmount(
            @Parameter(hidden = true)
            Member member,
            @RequestBody UpdateCupRequest updateCupRequest,
            @Parameter(description = "수정할 컵의 ID", required = true)
            @PathVariable Long cupId
    ) {
        cupService.update(
                cupId,
                member,
                updateCupRequest
        );
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "컵 삭제",
            description = "특정 컵을 삭제합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "컵 삭제 성공"
    )
    @ApiResponse(
            responseCode = "404",
            description = "컵을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = FailureBody.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 없음",
            content = @Content(schema = @Schema(implementation = FailureBody.class))
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true)
            Member member,
            @Parameter(description = "삭제할 컵의 ID", required = true)
            @PathVariable("id") Long id
    ) {
        cupService.delete(id, member);
        return ResponseEntity.noContent().build();
    }
}
