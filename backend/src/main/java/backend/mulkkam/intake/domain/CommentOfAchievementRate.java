package backend.mulkkam.intake.domain;

import backend.mulkkam.intake.domain.vo.AchievementRate;
import java.util.Arrays;
import java.util.Comparator;
import lombok.Getter;

@Getter
public enum CommentOfAchievementRate {

    FULL(new AchievementRate(100), "야호! 모두 달성했어요. 오늘은 내가 이 세상에서 제일 가는 하마"),
    MOSTLY(new AchievementRate(70), "거의 다 마셨어요! 조금만 더 힘내봐요"),
    HALF(new AchievementRate(50), "절반 이상 마셨어요! 좋아요"),
    LOW(new AchievementRate(30), "천리길도 한 걸음부터! 조금 더 마셔볼까요?"),
    VERY_LOW(new AchievementRate(10), "하뭉이는 아직 갈증나요..."),
    DEFAULT(new AchievementRate(0), "물 마시기는 정말 중요한 습관이랍니다!"),
    ;

    private final AchievementRate maxAchievementRate;
    private final String comment;

    CommentOfAchievementRate(
            AchievementRate maxAchievementRate,
            String comment
    ) {
        this.maxAchievementRate = maxAchievementRate;
        this.comment = comment;
    }

    public static String findCommentByAchievementRate(AchievementRate achievementRate) {
        double rate = achievementRate.value();

        return Arrays.stream(CommentOfAchievementRate.values())
                .filter(commentOfAchievementRate -> rate >= commentOfAchievementRate.maxAchievementRate.value())
                .max(Comparator.comparingDouble(
                        commentOfAchievementRate -> commentOfAchievementRate.maxAchievementRate.value()))
                .map(commentOfAchievementRate -> commentOfAchievementRate.comment)
                .orElse(DEFAULT.comment);
    }
}
