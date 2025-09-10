package backend.mulkkam.intake.domain;

import static org.assertj.core.api.Assertions.assertThat;

import backend.mulkkam.intake.domain.vo.AchievementRate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CommentOfAchievementRateTest {

    @DisplayName("달성률의 경계값을 바탕으로 코멘트를 반환할 때")
    @Nested
    class FindCommentByAchievementRate {

        @DisplayName("100퍼센트인 경우에 해당하는 문구가 반환된다")
        @Test
        void success_achievementRateIs100() {
            // given
            AchievementRate achievementRate = new AchievementRate(100.0);

            // when
            String comment = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

            // then
            assertThat(comment).contains(CommentOfAchievementRate.FULL.getComment());
        }

        @DisplayName("70퍼센트 이상이고 100퍼센트 미만인 경우에 해당하는 문구가 반환된다")
        @ParameterizedTest
        @ValueSource(doubles = {70.0, 70.1, 85.5, 99.9})
        void success_achievementRateIsBetween70And100(double rate) {
            // given
            AchievementRate achievementRate = new AchievementRate(rate);

            // when
            String comment = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

            // then
            assertThat(comment).contains(CommentOfAchievementRate.MOSTLY.getComment());
        }

        @DisplayName("50퍼센트 이상이고 70퍼센트 미만인 경우에 해당하는 문구가 반환된다")
        @ParameterizedTest
        @ValueSource(doubles = {50, 50.5, 69.9})
        void success_achievementRateIsBetween50And70(double rate) {
            // given
            AchievementRate achievementRate = new AchievementRate(rate);

            // when
            String comment = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

            // then
            assertThat(comment).contains(CommentOfAchievementRate.HALF.getComment());
        }

        @DisplayName("30퍼센트 이상이고 50퍼센트 미만인 경우에 해당하는 문구가 반환된다")
        @ParameterizedTest
        @ValueSource(doubles = {30, 40, 49})
        void success_achievementRateIsBetween30And50(double rate) {
            // given
            AchievementRate achievementRate = new AchievementRate(rate);

            // when
            String comment = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

            // then
            assertThat(comment).contains(CommentOfAchievementRate.LOW.getComment());
        }

        @DisplayName("10퍼센트 이상이고 30퍼센트 미만인 경우에 해당하는 문구가 반환된다")
        @ParameterizedTest
        @ValueSource(doubles = {10.1, 29.9, 15.3})
        void success_achievementRateIsBetween10And30(double rate) {
            // given
            AchievementRate achievementRate = new AchievementRate(rate);

            // when
            String comment = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

            // then
            assertThat(comment).contains(CommentOfAchievementRate.VERY_LOW.getComment());
        }

        @DisplayName("0퍼센트 이상이고 10퍼센트 미만인 경우에 해당하는 문구가 반환된다")
        @ParameterizedTest
        @ValueSource(doubles = {0.1, 9.9, 5.5})
        void success_achievementRateIsBetween0And10(double rate) {
            // given
            AchievementRate achievementRate = new AchievementRate(rate);

            // when
            String comment = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

            // then
            assertThat(comment).contains(CommentOfAchievementRate.DEFAULT.getComment());
        }
    }
}
