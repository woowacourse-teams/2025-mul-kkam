package backend.mulkkam.member.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.ModifyIsMarketingNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsNightNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsReminderEnabledRequest;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void modifyIsReminderEnabled(
            MemberDetails memberDetails,
            ModifyIsReminderEnabledRequest modifyIsReminderEnabledRequest
    ) {
        Member member = getMember(memberDetails.id());
        member.modifyIsReminderEnabled(modifyIsReminderEnabledRequest.isReminderEnabled());
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
        accountRefreshTokenRepository.deleteAllByAccount(account);
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

    private List<Long> findSuggestionNotificationIdsByMember(Member member) {
        return notificationRepository.findAllByMember(member).stream()
                .filter(notification -> notification.getNotificationType().equals(NotificationType.SUGGESTION))
                .map(Notification::getId)
                .toList();
    }
}
