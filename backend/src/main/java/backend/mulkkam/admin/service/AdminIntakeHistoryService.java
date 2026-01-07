package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.response.AdminIntakeHistoryDetailResponse;
import backend.mulkkam.admin.dto.response.AdminIntakeHistoryListResponse;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminIntakeHistoryService {

    private final IntakeHistoryRepository intakeHistoryRepository;

    @Transactional(readOnly = true)
    public Page<AdminIntakeHistoryListResponse> getIntakeHistories(Pageable pageable) {
        return intakeHistoryRepository.findAll(pageable)
                .map(AdminIntakeHistoryListResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminIntakeHistoryDetailResponse getIntakeHistory(Long intakeHistoryId) {
        IntakeHistory intakeHistory = intakeHistoryRepository.findById(intakeHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("IntakeHistory not found: " + intakeHistoryId));
        return AdminIntakeHistoryDetailResponse.from(intakeHistory);
    }

    @Transactional
    public void deleteIntakeHistory(Long intakeHistoryId) {
        IntakeHistory intakeHistory = intakeHistoryRepository.findById(intakeHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("IntakeHistory not found: " + intakeHistoryId));
        intakeHistoryRepository.delete(intakeHistory);
    }
}
