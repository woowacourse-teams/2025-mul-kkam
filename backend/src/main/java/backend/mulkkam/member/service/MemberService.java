package backend.mulkkam.member.service;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.OnboardingStatusResponse;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.dto.response.ProgressInfoResponse;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final OauthAccountRepository oauthAccountRepository;
    private final IntakeHistoryRepository intakeHistoryRepository;
    private final MemberRepository memberRepository;
    private final IntakeHistoryDetailRepository intakeDetailRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    public MemberResponse get(Member member) {
        return new MemberResponse(member);
    }

    @Transactional
    public void modifyPhysicalAttributes(
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest,
            Member member
    ) {
        member.updatePhysicalAttributes(physicalAttributesModifyRequest.toPhysicalAttributes());
        memberRepository.save(member);
    }

    public void validateDuplicateNickname(
            String nickname,
            Member member
    ) {
        if (member.isSameNickname(new MemberNickname(nickname))) {
            throw new CommonException(SAME_AS_BEFORE_NICKNAME);
        }
        if (memberRepository.existsByMemberNicknameValue(nickname)) {
            throw new CommonException(DUPLICATE_MEMBER_NICKNAME);
        }
    }

    public void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByMemberNicknameValue(nickname)) {
            throw new CommonException(DUPLICATE_MEMBER_NICKNAME);
        }
    }

    @Transactional
    public void modifyNickname(
            MemberNicknameModifyRequest memberNicknameModifyRequest,
            Member member
    ) {
        member.updateNickname(memberNicknameModifyRequest.toMemberNickname());
        memberRepository.save(member);
    }

    public MemberNicknameResponse getNickname(Member member) {
        return new MemberNicknameResponse(member.getMemberNickname());
    }

    @Transactional
    public void create(
            OauthAccount oauthAccount,
            CreateMemberRequest createMemberRequest
    ) {
        Member member = createMemberRequest.toMember();
        memberRepository.save(member);

        oauthAccount.modifyMember(member);
        oauthAccountRepository.save(oauthAccount);

        TargetAmountSnapshot targetAmountSnapshot = new TargetAmountSnapshot(
                member,
                LocalDate.now(),
                new Amount(createMemberRequest.targetIntakeAmount())
        );
        targetAmountSnapshotRepository.save(targetAmountSnapshot);
    }

    public OnboardingStatusResponse checkOnboardingStatus(OauthAccount oauthAccount) {
        boolean finishedOnboarding = oauthAccount.finishedOnboarding();
        return new OnboardingStatusResponse(finishedOnboarding);
    }

    public ProgressInfoResponse getProgressInfo(
            Member member,
            LocalDate date
    ) {
        Optional<IntakeHistory> foundIntakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(member,
                date);
        if (foundIntakeHistory.isEmpty()) {
            Optional<Integer> latestTargetAmount = targetAmountSnapshotRepository.findLatestTargetAmountValueByMemberIdBeforeDate(
                    member.getId(), date);
            return latestTargetAmount.map(integer -> new ProgressInfoResponse(member, integer))
                    .orElseGet(() -> new ProgressInfoResponse(member));
        }

        List<IntakeHistoryDetail> details = intakeDetailRepository.findAllByMemberAndDateRange(
                member,
                date,
                date
        );
        IntakeHistory intakeHistory = foundIntakeHistory.get();
        Amount totalAmount = calculateTotalIntakeAmount(details);
        AchievementRate achievementRate = new AchievementRate(totalAmount, intakeHistory.getTargetAmount());
        return new ProgressInfoResponse(member, intakeHistory, achievementRate, totalAmount);
    }

    private Amount calculateTotalIntakeAmount(List<IntakeHistoryDetail> intakeHistoryDetails) {
        int total = intakeHistoryDetails
                .stream()
                .mapToInt(intakeHistoryDetail -> intakeHistoryDetail.getIntakeAmount().value())
                .sum();
        return new Amount(total);
    }
}
