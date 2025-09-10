package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.dto.CupRankDto;

import java.util.List;

public record CupsRanksResponse(List<CupRankDto> cups) {
}
