package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.domain.Cup;

public record CupResponse(
        Long id,
        String nickname,
        Integer amount
) {

    public CupResponse(Cup cup) {
        this(cup.getId(), cup.getNickname().value(), cup.getCupAmount().value());
    }
}
