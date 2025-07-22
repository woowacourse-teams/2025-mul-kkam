package backend.mulkkam.cup.service;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CupService {

    private final CupRepository cupRepository;
    private final MemberRepository memberRepository;

    public CupResponse create(CupRegisterRequest cupRegisterRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 멤버입니다."));
        List<Cup> cups = cupRepository.findAllByMemberId(memberId);

        validPossibleCreateNewCup(cupRegisterRequest.amount(), cups);

        Integer rank = cupRepository.findMaxRankByMemberId(memberId);
        Cup cup = new Cup(member, new CupNickname(cupRegisterRequest.nickname()), cupRegisterRequest.amount(), rank);
        Cup createdCup = cupRepository.save(cup);
        return new CupResponse(createdCup.getId(), createdCup.getNickname().value(), createdCup.getAmount());
    }

    private void validPossibleCreateNewCup(Integer amount, List<Cup> cups) {
        if (cups.size() >= 3) {
            throw new IllegalArgumentException("컵은 최대 3개까지 등록 가능합니다.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("컵 용량이 올바르지 않은 값입니다.");
        }
    }
}
