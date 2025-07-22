package backend.mulkkam.intake.service;

import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Service
public class IntakeHistoryService {

    private final IntakeHistoryRepository intakeHistoryRepository;
}
