package backend.mulkkam.member.dto.response;

import backend.mulkkam.member.domain.Member;

public record ProgressInfoResponse(
        String memberNickname,
        int streak,
        double achievementRate,
        int targetAmount,
        int totalAmount,
        String comment
) {
    public ProgressInfoResponse(
            Member member,
            String comment
    ) {
        this(
                member.getMemberNickname().value(),
                0,
                0.0,
                0,
                0,
                comment
        );
    }
}
