package backend.mulkkam.cup.support;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;
import java.util.List;

public class CupFactory {
    
    private CupFactory() {
    }

    public static List<Cup> createDefaultCups(Member member) {
        return List.of(
                new Cup(member, new CupNickname("종이컵"), new CupAmount(180), new CupRank(1), IntakeType.WATER, "emoji"),
                new Cup(member, new CupNickname("스타벅스 톨"), new CupAmount(354), new CupRank(2), IntakeType.WATER,
                        "emoji"),
                new Cup(member, new CupNickname("스타벅스 그란데"), new CupAmount(473), new CupRank(3), IntakeType.WATER,
                        "emoji")
        );
    }
}
