package backend.mulkkam.member.dto.response;

public record ProgressInfoResponse(
        String memberNickname,
        int streak,
        Double achievementRate,
        Integer targetAmount,
        Integer totalAmount
) {
}
