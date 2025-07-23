package backend.mulkkam.cup.service;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CupService {

    private static final int DEFAULT_RANK = 0;
    private static final int MAX_CUP_COUNT = 3;
    private static final int MIN_CUP_SIZE = 0;
    private static final int CUP_RANK_OFFSET = 1;

    private final CupRepository cupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CupResponse create(CupRegisterRequest cupRegisterRequest, Long memberId) {
        Member member = getMember(memberId);
        List<Cup> cups = cupRepository.findAllByMemberId(memberId);

        validPossibleCreateNewCup(cupRegisterRequest.amount(), cups);

        Integer rank = cupRepository.findMaxRankByMemberId(memberId)
                .orElse(DEFAULT_RANK);

        Cup cup = new Cup(member, new CupNickname(cupRegisterRequest.nickname()),
                new Amount(cupRegisterRequest.amount()),
                rank + CUP_RANK_OFFSET);

        Cup createdCup = cupRepository.save(cup);
        return new CupResponse(
                createdCup.getId(),
                createdCup.getNickname().value(),
                createdCup.getAmount().value()
        );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 멤버입니다."));
    }

    private void validPossibleCreateNewCup(Integer amount, List<Cup> cups) {
        if (cups.size() >= MAX_CUP_COUNT) {
            throw new IllegalArgumentException("컵은 최대 3개까지 등록 가능합니다.");
        }
        if (amount <= MIN_CUP_SIZE) {
            throw new IllegalArgumentException("컵 용량이 올바르지 않은 값입니다.");
        }
    }
}
