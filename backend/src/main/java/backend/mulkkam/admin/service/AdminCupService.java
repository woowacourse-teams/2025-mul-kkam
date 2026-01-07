package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.request.UpdateAdminCupRequest;
import backend.mulkkam.admin.dto.response.GetAdminCupDetailResponse;
import backend.mulkkam.admin.dto.response.GetAdminCupListResponse;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.repository.CupEmojiRepository;
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
    private final CupEmojiRepository cupEmojiRepository;

    @Transactional(readOnly = true)
    public Page<GetAdminCupListResponse> getCups(Pageable pageable) {
        return cupRepository.findAll(pageable)
                .map(GetAdminCupListResponse::from);
    }

    @Transactional(readOnly = true)
    public GetAdminCupDetailResponse getCup(Long cupId) {
        Cup cup = cupRepository.findById(cupId)
                .orElseThrow(() -> new IllegalArgumentException("Cup not found: " + cupId));
        return GetAdminCupDetailResponse.from(cup);
    }

    @Transactional
    public void updateCup(Long cupId, UpdateAdminCupRequest request) {
        Cup cup = cupRepository.findById(cupId)
                .orElseThrow(() -> new IllegalArgumentException("Cup not found: " + cupId));
        CupEmoji cupEmoji = cupEmojiRepository.findById(request.cupEmojiId())
                .orElseThrow(() -> new IllegalArgumentException("CupEmoji not found: " + request.cupEmojiId()));
        
        cup.update(
                new CupNickname(request.nickname()),
                new CupAmount(request.cupAmount()),
                request.intakeType(),
                cupEmoji
        );
    }

    @Transactional
    public void deleteCup(Long cupId) {
        Cup cup = cupRepository.findById(cupId)
                .orElseThrow(() -> new IllegalArgumentException("Cup not found: " + cupId));
        cupRepository.delete(cup);
    }
}
