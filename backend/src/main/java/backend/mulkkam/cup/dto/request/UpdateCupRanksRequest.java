package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.dto.UpdateCupRankDto;

import java.util.List;

public record UpdateCupRanksRequest(List<UpdateCupRankDto> cups) {
}
