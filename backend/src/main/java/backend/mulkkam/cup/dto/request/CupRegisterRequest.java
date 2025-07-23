package backend.mulkkam.cup.dto.request;

public record CupRegisterRequest(
        String cupNickname,
        Integer amount
) {
}
