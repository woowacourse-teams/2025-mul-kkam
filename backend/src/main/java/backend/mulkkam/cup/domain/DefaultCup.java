package backend.mulkkam.cup.domain;

import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import lombok.Getter;

@Getter
public enum DefaultCup {

    WATER_PAPER_CUP(
            new CupNickname("종이컵"),
            new CupAmount(180),
            new CupRank(1),
            IntakeType.WATER,
            CupEmojiUrl.getDefaultByType(IntakeType.WATER)
    ),
    COFFEE_STARBUCKS_TALL(
            new CupNickname("스타벅스 톨"),
            new CupAmount(354),
            new CupRank(2),
            IntakeType.COFFEE,
            CupEmojiUrl.getDefaultByType(IntakeType.COFFEE)
    ),
    ;

    private final CupNickname nickname;
    private final CupAmount amount;
    private final CupRank rank;
    private final IntakeType intakeType;
    private final CupEmojiUrl cupEmojiUrl;

    DefaultCup(CupNickname nickname, CupAmount amount, CupRank rank, IntakeType intakeType, CupEmojiUrl cupEmojiUrl) {
        this.nickname = nickname;
        this.amount = amount;
        this.rank = rank;
        this.intakeType = intakeType;
        this.cupEmojiUrl = cupEmojiUrl;
    }
}
