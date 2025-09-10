package backend.mulkkam.member.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_OAUTH_ACCOUNT;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.OauthAccountDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.service.CupService;
import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.OnboardingStatusResponse;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OnboardingService {

    private final CupService cupService;
    private final MemberRepository memberRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;
    private final OauthAccountRepository oauthAccountRepository;

    @Transactional
    public void create(
            OauthAccountDetails accountDetails,
            CreateMemberRequest createMemberRequest
    ) {
        OauthAccount account = getOauthAccount(accountDetails);

        Member member = createMemberRequest.toMember();
        memberRepository.save(member);

        account.modifyMember(member);

        TargetAmountSnapshot targetAmountSnapshot = new TargetAmountSnapshot(
                member,
                LocalDate.now(),
                new TargetAmount(createMemberRequest.targetIntakeAmount())
        );
        targetAmountSnapshotRepository.save(targetAmountSnapshot);

        cupService.createAll(createMemberRequest.createCupRequests(), member);
    }

    public OnboardingStatusResponse checkOnboardingStatus(OauthAccountDetails accountDetails) {
        OauthAccount oauthAccount = getOauthAccount(accountDetails);
        boolean finishedOnboarding = oauthAccount.finishedOnboarding();
        return new OnboardingStatusResponse(finishedOnboarding);
    }

    private OauthAccount getOauthAccount(OauthAccountDetails accountDetails) {
        return oauthAccountRepository.findById(accountDetails.id())
                .orElseThrow(() -> new CommonException(NOT_FOUND_OAUTH_ACCOUNT));
    }
}
