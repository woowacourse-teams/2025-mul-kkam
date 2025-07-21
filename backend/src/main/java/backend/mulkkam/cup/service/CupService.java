package backend.mulkkam.cup.service;

import backend.mulkkam.cup.repository.CupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CupService {

    private final CupRepository cupRepository;
}
