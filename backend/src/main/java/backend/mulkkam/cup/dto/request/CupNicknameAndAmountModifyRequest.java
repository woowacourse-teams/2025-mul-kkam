package backend.mulkkam.cup.dto.request;

public record CupNicknameAndAmountModifyRequest(
        String cupNickname,
        Integer cupAmount
) {
}
