package backend.mulkkam.admin.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP_EMOJI;

import backend.mulkkam.admin.dto.request.UpdateAdminCupRequest;
import backend.mulkkam.admin.dto.response.GetAdminCupDetailResponse;
import backend.mulkkam.admin.dto.response.GetAdminCupListResponse;
import backend.mulkkam.common.exception.CommonException;
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
@Transactional(readOnly = true)
public class AdminCupService {

    private final CupRepository cupRepository;
    private final CupEmojiRepository cupEmojiRepository;

    public Page<GetAdminCupListResponse> getCups(Pageable pageable) {
        return cupRepository.findAll(pageable)
                .map(GetAdminCupListResponse::from);
    }

    public GetAdminCupDetailResponse getCup(Long cupId) {
        Cup cup = cupRepository.findById(cupId).orElseThrow(() -> new CommonException(NOT_FOUND_CUP));
        return GetAdminCupDetailResponse.from(cup);
    }

    @Transactional
    public void updateCup(
            Long cupId,
            UpdateAdminCupRequest request
    ) {
        Cup cup = cupRepository.findById(cupId).orElseThrow(() -> new CommonException(NOT_FOUND_CUP));
        CupEmoji cupEmoji = cupEmojiRepository.findById(request.cupEmojiId())
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP_EMOJI));

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
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP));
        cupRepository.delete(cup);
    }
}
