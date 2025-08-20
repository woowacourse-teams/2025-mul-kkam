package backend.mulkkam.cup.service;

import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.dto.CupEmojiResponse;
import backend.mulkkam.cup.dto.CupEmojisResponse;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CupEmojiService {

    private final CupEmojiRepository cupEmojiRepository;

    public CupEmojisResponse readAll() {
        List<CupEmoji> cupEmojis = cupEmojiRepository.findAll();

        return new CupEmojisResponse(toCupEmojiResponse(cupEmojis));
    }

    private static List<CupEmojiResponse> toCupEmojiResponse(List<CupEmoji> cupEmojis) {
        List<CupEmojiResponse> cupEmojiResponses = new ArrayList<>();
        for (CupEmoji cupEmoji : cupEmojis) {
            CupEmojiResponse cupEmojiResponse = new CupEmojiResponse(cupEmoji);
            cupEmojiResponses.add(cupEmojiResponse);
        }
        return cupEmojiResponses;
    }
}
