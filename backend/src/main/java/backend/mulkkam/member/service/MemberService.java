package backend.mulkkam.member.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.intake.dto.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.service.IntakeHistoryService;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.ProgressInfoResponse;
import backend.mulkkam.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final IntakeHistoryRepository intakeHistoryRepository;
    private final IntakeHistoryService intakeHistoryService;

    @Transactional
    public void modifyPhysicalAttributes(
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest,
            Long memberId
    ) {
        Member member = getById(memberId);
        member.updatePhysicalAttributes(physicalAttributesModifyRequest.toPhysicalAttributes());
    }

    public ProgressInfoResponse getTodayProgressInfo(DateRangeRequest date, Long memberId) {
        Member member = getById(memberId);
        String nickname = member.getMemberNickname().value();
        int targetAmount = member.getTargetAmount().value();

        List<IntakeHistorySummaryResponse> summaries = intakeHistoryService.readSummaryOfIntakeHistories(
                date,
                memberId
        );
        int streak = intakeHistoryRepository.countConsecutiveDays(memberId, date.from());
        return buildProgressInfoResponse(
                nickname,
                targetAmount,
                streak,
                summaries
        );
    }

    private ProgressInfoResponse buildProgressInfoResponse(
            String nickname,
            int targetAmount,
            int streak,
            List<IntakeHistorySummaryResponse> summaries
    ) {
        if (summaries.isEmpty()) {
            return new ProgressInfoResponse(
                    nickname,
                    streak,
                    0.0,
                    targetAmount,
                    0
            );
        }
        IntakeHistorySummaryResponse summary = summaries.getFirst();
        return new ProgressInfoResponse(
                nickname,
                streak,
                summary.achievementRate(),
                summary.targetAmount(),
                summary.totalIntakeAmount()
        );
    }

    private Member getById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }
}
