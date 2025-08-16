package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_SUGGESTION_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountByRecommendRequest;
import backend.mulkkam.intake.service.IntakeAmountService;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.NotificationFixtureBuilder;
import backend.mulkkam.support.SuggestionNotificationFixtureBuilder;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuggestionNotificationUnitServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SuggestionNotificationRepository suggestionNotificationRepository;

    @Mock
    private IntakeAmountService intakeAmountService;

    @InjectMocks
    private SuggestionNotificationService suggestionNotificationService;

    private final Long memberId = 1L;
    private final Member member = MemberFixtureBuilder.builder().buildWithId(memberId);

    @DisplayName("제안 알림에서 적용 요청할 때")
    @Nested
    class ApplyTargetAmount {

        @DisplayName("올바른 데이터라면 목표량 적용 상태가 true로 변경된다")
        @Test
        void success_validInput() {
            // given
            Notification notification = NotificationFixtureBuilder.withMember(member)
                .build();
            SuggestionNotification suggestionNotification = SuggestionNotificationFixtureBuilder
                .withNotification(notification)
                .recommendedTargetAmount(2000)
                .build();

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(suggestionNotificationRepository.findByIdAndNotificationMember(10L, member))
                .thenReturn(Optional.of(suggestionNotification));

            // when
            suggestionNotificationService.applyTargetAmount(10L, new MemberDetails(memberId));

            // then
            verify(intakeAmountService, times(1))
                .modifyDailyTargetBySuggested(member, new ModifyIntakeTargetAmountByRecommendRequest(new TargetAmount(2000)));
            assertThat(suggestionNotification.isApplyTargetAmount()).isTrue();
        }

        @DisplayName("존재하지 않는 멤버 id로 요청하면 예외를 발생한다")
        @Test
        void error_byNonExistingSuggestionMemberId() {
            // given
            when(memberRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                    () -> suggestionNotificationService.applyTargetAmount(1L,
                            new MemberDetails(999L))
            ).isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_MEMBER.name());
        }

        @DisplayName("존재하지 않는 제안 알림 id로 요청하면 예외를 발생한다")
        @Test
        void error_byNonExistingSuggestionNotificationId() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(suggestionNotificationRepository.findByIdAndNotificationMember(999L, member)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                    () -> suggestionNotificationService.applyTargetAmount(999L,
                            new MemberDetails(memberId))
            ).isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_SUGGESTION_NOTIFICATION.name());
        }
    }
}
