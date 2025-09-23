package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_SUGGESTION_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountBySuggestionRequest;
import backend.mulkkam.intake.service.IntakeAmountService;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.City;
import backend.mulkkam.notification.domain.CityDateTime;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import backend.mulkkam.support.fixture.NotificationFixtureBuilder;
import backend.mulkkam.support.fixture.SuggestionNotificationFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

@ExtendWith(MockitoExtension.class)
class SuggestionNotificationUnitServiceTest {

    @Mock
    private SuggestionNotificationRepository suggestionNotificationRepository;

    @Mock
    private IntakeAmountService intakeAmountService;

    @Mock
    private WeatherService weatherService;

    @Mock
    private MemberRepository memberRepository;

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
                    .recommendedTargetAmount(2_000)
                    .build();

            when(suggestionNotificationRepository.findByIdAndNotificationMemberId(10L, memberId))
                    .thenReturn(Optional.of(suggestionNotification));

            // when
            suggestionNotificationService.applyTargetAmount(10L, new MemberDetails(memberId));

            // then
            verify(intakeAmountService, times(1))
                    .modifyDailyTargetBySuggested(new MemberDetails(memberId),
                            new ModifyIntakeTargetAmountBySuggestionRequest(2_000));
            assertThat(suggestionNotification.isApplyTargetAmount()).isTrue();
        }

        @DisplayName("존재하지 않는 멤버 id로 요청하면 예외를 발생한다")
        @Test
        void error_byNonExistingSuggestionMemberId() {
            // when & then
            assertThatThrownBy(
                    () -> suggestionNotificationService.applyTargetAmount(1L,
                            new MemberDetails(999L))
            ).isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_SUGGESTION_NOTIFICATION.name());
        }

        @DisplayName("존재하지 않는 제안 알림 id로 요청하면 예외를 발생한다")
        @Test
        void error_byNonExistingSuggestionNotificationId() {
            // given
            when(suggestionNotificationRepository.findByIdAndNotificationMemberId(999L, memberId)).thenReturn(
                    Optional.empty());

            // when & then
            assertThatThrownBy(
                    () -> suggestionNotificationService.applyTargetAmount(999L,
                            new MemberDetails(memberId))
            ).isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_SUGGESTION_NOTIFICATION.name());
        }
    }

    @DisplayName("날씨 알림 스케줄링할 때에")
    @Nested
    class NotifyAdditionalWaterIntakeByWeather {

        @DisplayName("매일 오전 8시에 실행되어야 한다")
        @Test
        void success_shouldRunEveryDayAt8AM() {
            // given
            String cron = "0 0 8 * * *";
            ZoneId zone = ZoneId.of(City.SEOUL.getZoneId());
            TimeZone tz = TimeZone.getTimeZone(zone);

            CronTrigger trigger = new CronTrigger(cron, tz);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(zone);

            Instant start = LocalDateTime.of(2025, 9, 23, 0, 0, 0).atZone(zone).toInstant();
            List<String> expectedTimes = List.of(
                    "2025/09/23 08:00:00",
                    "2025/09/24 08:00:00",
                    "2025/09/25 08:00:00"
            );

            // when
            SimpleTriggerContext context = new SimpleTriggerContext();
            Date startDate = Date.from(start);
            context.update(startDate, startDate, startDate);

            // then
            for (String expected : expectedTimes) {
                Date next = trigger.nextExecutionTime(context);

                String actual = fmt.format(next.toInstant());
                assertThat(actual).isEqualTo(expected);

                context.update(next, next, next);
            }
        }

        @DisplayName("추천 음용량이 생기는 온도가 아니면 알림을 발송하지 않는다")
        @ParameterizedTest
        @ValueSource(doubles = {20.0, 26.0})
        void success_fast_fail(double averageTemperature) {
            // given
            when(weatherService.getAverageTemperature(any(CityDateTime.class)))
                    .thenAnswer(inv -> new AverageTemperature(inv.getArgument(0), averageTemperature));

            // when
            suggestionNotificationService.notifyAdditionalWaterIntakeByWeather();

            // then
            verify(memberRepository, never()).findAll();
            verifyNoInteractions(intakeAmountService);
            verifyNoInteractions(suggestionNotificationRepository);
        }
    }
}
