package backend.mulkkam.intake.service;

import backend.mulkkam.intake.dto.IntakeAmountUpdateRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeAmountService {

    private final MemberRepository memberRepository;

    @Transactional
    public void updateTarget(
            IntakeAmountUpdateRequest intakeAmountUpdateRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);

        member.updateTargetAmount(intakeAmountUpdateRequest.toAmount());
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다."));
    }
}
