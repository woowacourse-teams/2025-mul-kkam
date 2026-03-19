package backend.mulkkam.admin.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;

import backend.mulkkam.admin.dto.request.UpdateAdminIntakeHistoryRequest;
import backend.mulkkam.admin.dto.response.GetAdminIntakeHistoryDetailResponse;
import backend.mulkkam.admin.dto.response.GetAdminIntakeHistoryListResponse;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.vo.TargetAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminIntakeHistoryService {

    private final IntakeHistoryRepository intakeHistoryRepository;

    public Page<GetAdminIntakeHistoryListResponse> getIntakeHistories(Pageable pageable) {
        return intakeHistoryRepository.findAll(pageable)
                .map(GetAdminIntakeHistoryListResponse::from);
    }

    public GetAdminIntakeHistoryDetailResponse getIntakeHistory(Long intakeHistoryId) {
        IntakeHistory intakeHistory = intakeHistoryRepository.findById(intakeHistoryId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY));
        return GetAdminIntakeHistoryDetailResponse.from(intakeHistory);
    }

    @Transactional
    public void updateIntakeHistory(
            Long intakeHistoryId,
            UpdateAdminIntakeHistoryRequest request
    ) {
        IntakeHistory intakeHistory = intakeHistoryRepository.findById(intakeHistoryId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY));
        intakeHistory.modifyTargetAmount(new TargetAmount(request.targetAmount()));
    }

    @Transactional
    public void deleteIntakeHistory(Long intakeHistoryId) {
        IntakeHistory intakeHistory = intakeHistoryRepository.findById(intakeHistoryId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY));
        intakeHistoryRepository.delete(intakeHistory);
    }
}
