package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.domain.Cup;
import java.util.List;

public record CupsResponse(
        int size,
        List<CupResponse> cups
) {
    public CupsResponse(List<Cup> cup) {
        this(
                cup.size(),
                cup.stream()
                        .map(CupResponse::new).toList());
    }
}
