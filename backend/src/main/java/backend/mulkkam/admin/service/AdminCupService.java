package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.response.AdminCupDetailResponse;
import backend.mulkkam.admin.dto.response.AdminCupListResponse;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.repository.CupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminCupService {

    private final CupRepository cupRepository;

    @Transactional(readOnly = true)
    public Page<AdminCupListResponse> getCups(Pageable pageable) {
        return cupRepository.findAll(pageable)
                .map(AdminCupListResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminCupDetailResponse getCup(Long cupId) {
        Cup cup = cupRepository.findById(cupId)
                .orElseThrow(() -> new IllegalArgumentException("Cup not found: " + cupId));
        return AdminCupDetailResponse.from(cup);
    }

    @Transactional
    public void deleteCup(Long cupId) {
        Cup cup = cupRepository.findById(cupId)
                .orElseThrow(() -> new IllegalArgumentException("Cup not found: " + cupId));
        cupRepository.delete(cup);
    }
}
