package backend.mulkkam.cup.service;

import backend.mulkkam.cup.repository.CupRepository;
import org.springframework.stereotype.Service;

@Service
public class CupService {

    private final CupRepository cupRepository;

    public CupService(CupRepository cupRepository) {
        this.cupRepository = cupRepository;
    }
}
