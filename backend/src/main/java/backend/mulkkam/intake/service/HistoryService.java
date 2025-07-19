package backend.mulkkam.intake.service;

import backend.mulkkam.intake.repository.HistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }
}
