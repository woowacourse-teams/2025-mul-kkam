package backend.mulkkam.cup.service;

import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.DefaultCup;
import backend.mulkkam.cup.dto.CupEmojiResponse;
import backend.mulkkam.cup.dto.CupEmojisResponse;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CupEmojiService {

    private final CupEmojiRepository cupEmojiRepository;

    public CupEmojisResponse readAll() {
        List<CupEmoji> cupEmojis = cupEmojiRepository.findAllOrderByCode(DefaultCup.getHighestPriorityEmojiCode());
        return new CupEmojisResponse(toCupEmojiResponse(cupEmojis));
    }

    private List<CupEmojiResponse> toCupEmojiResponse(List<CupEmoji> cupEmojis) {
        return cupEmojis.stream().map(CupEmojiResponse::new)
                .collect(Collectors.toList());
    }
}
