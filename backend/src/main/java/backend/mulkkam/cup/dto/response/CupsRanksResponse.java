package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.dto.UpdateCupRankDto;

import java.util.List;

public record CupsRanksResponse(List<UpdateCupRankDto> cups) {
}
