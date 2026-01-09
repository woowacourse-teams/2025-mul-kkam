package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_SUGGESTION_NOTIFICATION;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberRole;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.fixture.notification.NotificationFixtureBuilder;
import backend.mulkkam.support.fixture.notification.SuggestionNotificationFixtureBuilder;
import backend.mulkkam.support.service.ServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SuggestionNotificationServiceTest extends ServiceTest {

    @Autowired
    private SuggestionNotificationService suggestionNotificationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SuggestionNotificationRepository suggestionNotificationRepository;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    private Member createAndSaveMember() {
        Member member = MemberFixtureBuilder.builder()
                .weight(60.0)
                .build();
        return memberRepository.save(member);
    }

    @DisplayName("제안 알림에서 적용 요청할 때")
    @Nested
    class ApplyTargetAmount {

        @DisplayName("올바른 데이터라면 목표량이 정상적으로 적용된다")
        @Test
        void success_validInput() {
            // given
            Member savedMember = createAndSaveMember();
            Notification notification = NotificationFixtureBuilder.withMember(savedMember)
                    .build();
            SuggestionNotification savedSuggestionNotification = suggestionNotificationRepository.save(
                    SuggestionNotificationFixtureBuilder.withNotification(notification)
                            .recommendedTargetAmount(2_000)
                            .build());

            // when
            suggestionNotificationService.applyTargetAmount(savedSuggestionNotification.getId(),
                    new MemberDetails(savedMember.getId(), MemberRole.MEMBER));

            // then
            SuggestionNotification actual = suggestionNotificationRepository.findById(
                    savedSuggestionNotification.getId()).get();
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(savedMember);

            assertSoftly(softly -> {
                softly.assertThat(actual.isApplyTargetAmount()).isTrue();
                softly.assertThat(intakeHistories.getFirst().getTargetAmount())
                        .isEqualTo(new TargetAmount(3_000));
            });
        }

        @DisplayName("존재하지 않는 제안 알림 id로 요청하면 예외를 발생한다")
        @Test
        void fail_byNonExistingSuggestionNotificationId() {
            // given
            Member savedMember = createAndSaveMember();

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> suggestionNotificationService.applyTargetAmount(Long.MAX_VALUE,
                            new MemberDetails(savedMember.getId(), MemberRole.MEMBER)));
            assertEquals(NOT_FOUND_SUGGESTION_NOTIFICATION, ex.getErrorCode());
        }
    }
}
