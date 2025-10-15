package backend.mulkkam.cup.domain;

import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum DefaultCup {

    WATER_PAPER_CUP(
            new CupNickname("종이컵"),
            new CupAmount(180),
            new CupRank(1),
            IntakeType.WATER,
            EmojiCode.of(IntakeType.WATER, EmojiType.DEFAULT)
    ),
    COFFEE_STARBUCKS_TALL(
            new CupNickname("스타벅스 톨"),
            new CupAmount(354),
            new CupRank(2),
            IntakeType.COFFEE,
            EmojiCode.of(IntakeType.COFFEE, EmojiType.DEFAULT)
    ),
    ;

    private final CupNickname nickname;
    private final CupAmount amount;
    private final CupRank rank;
    private final IntakeType intakeType;
    private final EmojiCode code;

     DefaultCup(CupNickname nickname, CupAmount amount, CupRank rank, IntakeType intakeType, EmojiCode code) {
        this.nickname = nickname;
        this.amount = amount;
        this.rank = rank;
        this.intakeType = intakeType;
        this.code = code;
    }

    public static Optional<DefaultCup> of(IntakeType type) {
        return Arrays.stream(values())
                .filter(v -> v.intakeType == type)
                .findFirst();
    }
}
