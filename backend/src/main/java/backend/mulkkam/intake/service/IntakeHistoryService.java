package backend.mulkkam.intake.service;

import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class IntakeHistoryService {

    private final IntakeHistoryRepository intakeHistoryRepository;

    public IntakeHistoryService(IntakeHistoryRepository intakeHistoryRepository) {
        this.intakeHistoryRepository = intakeHistoryRepository;
    }
}
