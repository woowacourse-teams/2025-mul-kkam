package backend.mulkkam.member.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_OAUTH_ACCOUNT;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.dto.OauthAccountDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.DefaultCup;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.OnboardingStatusResponse;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.ModifyIsMarketingNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsNightNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.dto.response.NotificationSettingsResponse;
import backend.mulkkam.member.dto.response.ProgressInfoResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final OauthAccountRepository oauthAccountRepository;
    private final IntakeHistoryRepository intakeHistoryRepository;
    private final MemberRepository memberRepository;
    private final IntakeHistoryDetailRepository intakeDetailRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;
    private final CupRepository cupRepository;
    private final DeviceRepository deviceRepository;
    private final IntakeHistoryDetailRepository intakeHistoryDetailRepository;
    private final NotificationRepository notificationRepository;
    private final SuggestionNotificationRepository suggestionNotificationRepository;
    private final CupEmojiRepository cupEmojiRepository;

    public MemberResponse get(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
        return new MemberResponse(member);
    }

    @Transactional
    public void modifyPhysicalAttributes(
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        member.updatePhysicalAttributes(physicalAttributesModifyRequest.toPhysicalAttributes());
    }

    public void validateDuplicateNickname(
            String nickname,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        if (member.isSameNickname(nickname)) {
            throw new CommonException(SAME_AS_BEFORE_NICKNAME);
        }
        validateDuplicateNickname(nickname);
    }

    @Transactional
    public void modifyNickname(
            MemberNicknameModifyRequest memberNicknameModifyRequest,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        validateDuplicateNickname(memberNicknameModifyRequest.memberNickname());

        member.updateNickname(memberNicknameModifyRequest.toMemberNickname());
    }

    public void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByActiveNickname(nickname)) {
            throw new CommonException(DUPLICATE_MEMBER_NICKNAME);
        }
    }

    public MemberNicknameResponse getNickname(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
        return new MemberNicknameResponse(member.getMemberNickname());
    }

    @Transactional
    public void create(
            OauthAccountDetails accountDetails,
            CreateMemberRequest createMemberRequest
    ) {
        Member member = createMemberRequest.toMember();
        memberRepository.save(member);
        OauthAccount account = getOauthAccount(accountDetails);
        account.modifyMember(member);

        TargetAmountSnapshot targetAmountSnapshot = new TargetAmountSnapshot(
                member,
                LocalDate.now(),
                new TargetAmount(createMemberRequest.targetIntakeAmount())
        );
        targetAmountSnapshotRepository.save(targetAmountSnapshot);
        cupRepository.saveAll(getDefaultCups(member));
    }

    public OnboardingStatusResponse checkOnboardingStatus(OauthAccountDetails accountDetails) {
        OauthAccount oauthAccount = getOauthAccount(accountDetails);
        boolean finishedOnboarding = oauthAccount.finishedOnboarding();
        return new OnboardingStatusResponse(finishedOnboarding);
    }

    public ProgressInfoResponse getProgressInfo(
            MemberDetails memberDetails,
            LocalDate date
    ) {
        Member member = getMember(memberDetails.id());

        Optional<IntakeHistory> foundIntakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(member, date);
        if (foundIntakeHistory.isEmpty()) {
            int streak = findStreak(member, date);
            return new ProgressInfoResponse(member, streak);
        }

        List<IntakeHistoryDetail> details = intakeDetailRepository.findAllByMemberAndDateRange(
                member,
                date,
                date
        );
        IntakeHistory intakeHistory = foundIntakeHistory.get();
        int totalAmount = calculateTotalIntakeAmount(details);
        AchievementRate achievementRate = new AchievementRate(totalAmount, intakeHistory.getTargetAmount());
        return new ProgressInfoResponse(member, intakeHistory, achievementRate, totalAmount);
    }

    @Transactional
    public void modifyIsNightNotificationAgreed(
            MemberDetails memberDetails,
            ModifyIsNightNotificationAgreedRequest modifyIsNightNotificationAgreedRequest
    ) {
        Member member = getMember(memberDetails.id());
        member.modifyIsNightNotificationAgreed(modifyIsNightNotificationAgreedRequest.isNightNotificationAgreed());
    }

    @Transactional
    public void modifyIsMarketingNotificationAgreed(
            MemberDetails memberDetails,
            ModifyIsMarketingNotificationAgreedRequest modifyIsMarketingNotificationAgreedRequest
    ) {
        Member member = getMember(memberDetails.id());
        member.modifyIsMarketingNotificationAgreed(
                modifyIsMarketingNotificationAgreedRequest.isMarketingNotificationAgreed()
        );
    }

    @Transactional
    public void delete(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());

        oauthAccountRepository.findByMember(member)
                .ifPresent((this::deleteRefreshTokenAndAccount));
        deviceRepository.deleteByMember(member);

        List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(member);
        intakeHistories.forEach(intakeHistoryDetailRepository::deleteByIntakeHistory);
        cupRepository.deleteByMember(member);

        intakeHistoryRepository.deleteByMember(member);

        targetAmountSnapshotRepository.deleteByMember(member);

        List<Long> notificationIds = findSuggestionNotificationIdsByMember(member);
        suggestionNotificationRepository.deleteByIdIn(notificationIds);
        notificationRepository.deleteByMember(member);

        memberRepository.delete(member);
    }

    private void deleteRefreshTokenAndAccount(OauthAccount account) {
        accountRefreshTokenRepository.findByAccount(account)
                .ifPresent(accountRefreshTokenRepository::delete);
        oauthAccountRepository.delete(account);
    }

    public NotificationSettingsResponse getNotificationSettings(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
        return new NotificationSettingsResponse(member);
    }

    private int calculateTotalIntakeAmount(List<IntakeHistoryDetail> intakeHistoryDetails) {
        return intakeHistoryDetails
                .stream()
                .mapToInt(intakeHistoryDetail -> intakeHistoryDetail.getIntakeAmount().value())
                .sum();
    }

    private int findStreak(Member member, LocalDate todayDate) {
        LocalDate yesterday = todayDate.minusDays(1);
        return intakeHistoryRepository.findByMemberAndHistoryDate(member, yesterday)
                .map(intakeHistory -> intakeHistory.getStreak() + 1)
                .orElse(1);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }

    private OauthAccount getOauthAccount(OauthAccountDetails accountDetails) {
        return oauthAccountRepository.findById(accountDetails.id())
                .orElseThrow(() -> new CommonException(NOT_FOUND_OAUTH_ACCOUNT));
    }

    private List<Long> findSuggestionNotificationIdsByMember(Member member) {
        return notificationRepository.findAllByMember(member).stream()
                .filter(notification -> notification.getNotificationType().equals(NotificationType.SUGGESTION))
                .map(Notification::getId)
                .toList();
    }

    // TODO: CupService 내부 로직과 중복 - 서비스로 묶는 리팩터링시 적용
    private List<Cup> getDefaultCups(Member member) {
        Map<CupEmojiUrl, CupEmoji> emojiByUrl = getDefaultEmojiByUrl();
        return Arrays.stream(DefaultCup.values())
                .map(defaultCup -> {
                    CupEmoji emoji = emojiByUrl.get(defaultCup.getCupEmojiUrl());
                    return new Cup(
                            member,
                            defaultCup.getNickname(),
                            defaultCup.getAmount(),
                            defaultCup.getRank(),
                            defaultCup.getIntakeType(),
                            emoji
                    );
                })
                .toList();
    }

    private Map<CupEmojiUrl, CupEmoji> getDefaultEmojiByUrl() {
        List<CupEmoji> defaultEmojis = getDefaultCupEmojis();
        return defaultEmojis.stream()
                .collect(Collectors.toMap(CupEmoji::getUrl, Function.identity()));
    }

    private List<CupEmoji> getDefaultCupEmojis() {
        List<CupEmojiUrl> defaultEmojiUrls = Arrays.stream(DefaultCup.values())
                .map(DefaultCup::getCupEmojiUrl)
                .distinct()
                .toList();
        return cupEmojiRepository.findAllByUrlIn(defaultEmojiUrls);
    }
}
