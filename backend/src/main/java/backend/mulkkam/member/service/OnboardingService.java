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
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.repository.NotificationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final NotificationRepository notificationRepository;

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

        notificationRepository.save(new Notification(
                NotificationType.NOTICE,
                "하뭉이가 보내는 알림은 세 가지예요.\n"
                        + "리마인드, 날씨, 그리고 운동 알림까지!\n"
                        + "리마인드는 오후 2시와 7시에 물 마시는 것을 잊지 않도록 알려드려요.😉\n"
                        + "날씨 알림은 하루 평균 기온이 26도를 넘을 때,\n"
                        + "추가로 마셔야 할 물의 양을 안내해요.\n"
                        + "운동 알림은 100kcal 이상을 소모했을 때, \n"
                        + "필요한 추가 수분 섭취량을 추천해 드린답니다. 📮",
                LocalDateTime.now(),
                member)
        );
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
