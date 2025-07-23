package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.domain.Cup;
import java.util.List;

public record CupsResponse(
        int size,
        List<CupResponse> cups
) {
    public CupsResponse(List<Cup> cupList) {
        this(
                cupList.size(),
                cupList.stream()
                        .map(cup -> new CupResponse(
                                cup.getId(),
                                cup.getNickname().value(),
                                cup.getCupAmount().value()))
                        .toList()
        );
    }
}
