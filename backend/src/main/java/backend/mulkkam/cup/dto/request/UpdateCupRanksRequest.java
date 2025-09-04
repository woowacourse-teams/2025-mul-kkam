package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.dto.CupRankDto;
import jakarta.validation.Valid;

import java.util.List;

public record UpdateCupRanksRequest(List<@Valid CupRankDto> cups) {
}
