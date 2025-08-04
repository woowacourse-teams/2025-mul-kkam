package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.dto.CupRankDto;

import java.util.List;

public record UpdateCupRanksRequest(List<CupRankDto> cups) {
}
