package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_REMINDER_SCHEDULE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_REMINDER_SCHEDULE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.ReminderSchedule;
import backend.mulkkam.notification.dto.request.CreateReminderScheduleRequest;
import backend.mulkkam.notification.dto.request.ModifyReminderScheduleTimeRequest;
import backend.mulkkam.notification.dto.response.ReadReminderSchedulesResponse;
import backend.mulkkam.notification.repository.ReminderScheduleRepository;
import backend.mulkkam.support.fixture.ReminderScheduleFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ReminderScheduleServiceUnitTest {

    @InjectMocks
    private ReminderScheduleService reminderScheduleService;

    @Mock
    private ReminderScheduleRepository reminderScheduleRepository;

    @Mock
    private NotificationBatchService notificationBatchService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationService notificationService;

    @DisplayName("리마인더 스케줄을 생성할 때")
    @Nested
    class Create {

        private final Long memberId = 1L;
        private final Member member = MemberFixtureBuilder.builder().buildWithId(memberId);
        private final MemberDetails memberDetails = new MemberDetails(memberId);

        @DisplayName("유효한 요청으로 리마인더 스케줄을 생성한다")
        @Test
        void success_whenValidRequest() {
            // given
            LocalTime schedule = LocalTime.of(14, 30);
            CreateReminderScheduleRequest request = new CreateReminderScheduleRequest(schedule);
            
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            // when
            reminderScheduleService.create(memberDetails, request);

            // then
            verify(reminderScheduleRepository).saveAndFlush(
                    argThat(reminderSchedule ->
                            reminderSchedule.getMember().equals(member) &&
                            reminderSchedule.getSchedule().equals(schedule))
            );
        }

        @DisplayName("존재하지 않는 멤버로 생성하면 예외가 발생한다")
        @Test
        void fail_whenMemberNotFound() {
            // given
            LocalTime schedule = LocalTime.of(14, 30);
            CreateReminderScheduleRequest request = new CreateReminderScheduleRequest(schedule);
            
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reminderScheduleService.create(memberDetails, request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_MEMBER.name());
        }

        @DisplayName("중복된 스케줄을 생성하면 예외가 발생한다")
        @Test
        void fail_whenDuplicateSchedule() {
            // given
            LocalTime schedule = LocalTime.of(14, 30);
            CreateReminderScheduleRequest request = new CreateReminderScheduleRequest(schedule);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(reminderScheduleRepository.saveAndFlush(any(ReminderSchedule.class)))
                    .thenThrow(new DataIntegrityViolationException("duplicate"));

            // when & then
            assertThatThrownBy(() -> reminderScheduleService.create(memberDetails, request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(DUPLICATED_REMINDER_SCHEDULE.name());
        }
    }

    @DisplayName("리마인더 스케줄 목록을 조회할 때")
    @Nested
    class Read {

        private final Long memberId = 1L;
        private final Member member = MemberFixtureBuilder.builder()
                .isReminderEnabled(true)
                .buildWithId(memberId);
        private final MemberDetails memberDetails = new MemberDetails(memberId);

        @DisplayName("멤버의 모든 리마인더 스케줄을 시간순으로 반환한다")
        @Test
        void success_whenValidMember() {
            // given
            ReminderSchedule schedule1 = ReminderScheduleFixtureBuilder
                    .withMember(member)
                    .schedule(LocalTime.of(9, 0))
                    .build();
            ReminderSchedule schedule2 = ReminderScheduleFixtureBuilder
                    .withMember(member)
                    .schedule(LocalTime.of(14, 30))
                    .build();
            ReminderSchedule schedule3 = ReminderScheduleFixtureBuilder
                    .withMember(member)
                    .schedule(LocalTime.of(19, 0))
                    .build();
            
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(member))
                    .thenReturn(List.of(schedule1, schedule2, schedule3));

            // when
            ReadReminderSchedulesResponse response = reminderScheduleService.read(memberDetails);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.isReminderEnabled()).isTrue();
                softly.assertThat(response.reminderSchedules()).hasSize(3);
                softly.assertThat(response.reminderSchedules().get(0).schedule()).isEqualTo(LocalTime.of(9, 0));
                softly.assertThat(response.reminderSchedules().get(1).schedule()).isEqualTo(LocalTime.of(14, 30));
                softly.assertThat(response.reminderSchedules().get(2).schedule()).isEqualTo(LocalTime.of(19, 0));
            });
        }

        @DisplayName("리마인더가 비활성화된 멤버는 isReminderEnabled가 false로 반환된다")
        @Test
        void success_whenReminderDisabled() {
            // given
            Member disabledMember = MemberFixtureBuilder.builder()
                    .isReminderEnabled(false)
                    .buildWithId(memberId);
            
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(disabledMember));
            when(reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(disabledMember))
                    .thenReturn(List.of());

            // when
            ReadReminderSchedulesResponse response = reminderScheduleService.read(memberDetails);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.isReminderEnabled()).isFalse();
                softly.assertThat(response.reminderSchedules()).isEmpty();
            });
        }

        @DisplayName("스케줄이 없는 멤버는 빈 리스트를 반환한다")
        @Test
        void success_whenNoSchedules() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(member))
                    .thenReturn(List.of());

            // when
            ReadReminderSchedulesResponse response = reminderScheduleService.read(memberDetails);

            // then
            assertThat(response.reminderSchedules()).isEmpty();
        }

        @DisplayName("존재하지 않는 멤버로 조회하면 예외가 발생한다")
        @Test
        void fail_whenMemberNotFound() {
            // given
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reminderScheduleService.read(memberDetails))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_MEMBER.name());
        }
    }

    @DisplayName("리마인더 스케줄 시간을 수정할 때")
    @Nested
    class ModifyTime {

        private final Long memberId = 1L;
        private final Long scheduleId = 10L;
        private final Member member = MemberFixtureBuilder.builder().buildWithId(memberId);
        private final MemberDetails memberDetails = new MemberDetails(memberId);

        @DisplayName("유효한 요청으로 스케줄 시간을 수정한다")
        @Test
        void success_whenValidRequest() {
            // given
            ReminderSchedule schedule = ReminderScheduleFixtureBuilder
                    .withMember(member)
                    .schedule(LocalTime.of(14, 0))
                    .build();
            LocalTime newTime = LocalTime.of(16, 30);
            ModifyReminderScheduleTimeRequest request = new ModifyReminderScheduleTimeRequest(scheduleId, newTime);
            
            when(reminderScheduleRepository.findByIdAndMemberId(scheduleId, memberId))
                    .thenReturn(Optional.of(schedule));

            // when
            reminderScheduleService.modifyTime(memberDetails, request);

            // then
            verify(reminderScheduleRepository).flush();
            assertThat(schedule.getSchedule()).isEqualTo(newTime);
        }

        @DisplayName("존재하지 않는 스케줄 ID로 수정하면 예외가 발생한다")
        @Test
        void fail_whenScheduleNotFound() {
            // given
            LocalTime newTime = LocalTime.of(16, 30);
            ModifyReminderScheduleTimeRequest request = new ModifyReminderScheduleTimeRequest(scheduleId, newTime);
            
            when(reminderScheduleRepository.findByIdAndMemberId(scheduleId, memberId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reminderScheduleService.modifyTime(memberDetails, request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_REMINDER_SCHEDULE.name());

        }

        @DisplayName("중복된 시간으로 수정하면 예외가 발생한다")
        @Test
        void fail_whenDuplicateTime() {
            // given
            ReminderSchedule schedule = ReminderScheduleFixtureBuilder
                    .withMember(member)
                    .schedule(LocalTime.of(14, 0))
                    .build();
            LocalTime newTime = LocalTime.of(16, 30);
            ModifyReminderScheduleTimeRequest request = new ModifyReminderScheduleTimeRequest(scheduleId, newTime);
            
            when(reminderScheduleRepository.findByIdAndMemberId(scheduleId, memberId))
                    .thenReturn(Optional.of(schedule));
            doThrow(new DataIntegrityViolationException("duplicate"))
                    .when(reminderScheduleRepository)
                    .flush();

            // when & then
            assertThatThrownBy(() -> reminderScheduleService.modifyTime(memberDetails, request))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(DUPLICATED_REMINDER_SCHEDULE.name());
        }
    }

    @DisplayName("리마인더 스케줄을 삭제할 때")
    @Nested
    class Delete {

        private final Long memberId = 1L;
        private final Long scheduleId = 10L;
        private final MemberDetails memberDetails = new MemberDetails(memberId);

        @DisplayName("유효한 요청으로 스케줄을 삭제한다")
        @Test
        void success_whenValidRequest() {
            // given
            when(reminderScheduleRepository.existsByIdAndMemberId(scheduleId, memberId))
                    .thenReturn(true);

            // when
            reminderScheduleService.delete(memberDetails, scheduleId);

            // then
            verify(reminderScheduleRepository).deleteById(scheduleId);
        }

        @DisplayName("존재하지 않는 스케줄 ID로 삭제하면 예외가 발생한다")
        @Test
        void fail_whenScheduleNotFound() {
            // given
            when(reminderScheduleRepository.existsByIdAndMemberId(scheduleId, memberId))
                    .thenReturn(false);

            // when & then
            assertThatThrownBy(() -> reminderScheduleService.delete(memberDetails, scheduleId))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_REMINDER_SCHEDULE.name());
            
            verify(reminderScheduleRepository, never()).deleteById(scheduleId);
        }

        @DisplayName("다른 사용자의 스케줄을 삭제하려고 하면 예외가 발생한다")
        @Test
        void fail_whenNotOwner() {
            // given
            when(reminderScheduleRepository.existsByIdAndMemberId(scheduleId, memberId))
                    .thenReturn(false);

            // when & then
            assertThatThrownBy(() -> reminderScheduleService.delete(memberDetails, scheduleId))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_REMINDER_SCHEDULE.name());
        }
    }

    @DisplayName("정기적으로 리마인더 알림을 전송할 때")
    @Nested
    class ExecuteReminderNotification {

        @DisplayName("현재 시각에 해당하는 활성화된 스케줄의 멤버에게 알림을 전송한다")
        @Test
        void success_whenSchedulesExist() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 1, 15, 14, 30);
            
            Member member1 = MemberFixtureBuilder.builder()
                    .isReminderEnabled(true)
                    .buildWithId(1L);
            Member member2 = MemberFixtureBuilder.builder()
                    .isReminderEnabled(true)
                    .buildWithId(2L);

            when(notificationBatchService.batchRead(
                    any(BiFunction.class),
                    any(Function.class),
                    eq(1000)
            )).thenReturn(List.of(member1.getId(), member2.getId()));

            // when
            reminderScheduleService.executeReminderNotification(now);

            // then
            verify(notificationService).processReminderNotifications(
                    argThat(list -> list.size() == 2 && 
                            list.contains(member1.getId()) &&
                            list.contains(member2.getId())),
                    any(LocalDateTime.class)
            );
        }

        @DisplayName("해당 시각에 스케줄이 없으면 알림을 전송하지 않는다")
        @Test
        void success_whenNoSchedules() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 1, 15, 14, 30);

            // when
            reminderScheduleService.executeReminderNotification(now);

            // then
            verify(notificationService, never()).processReminderNotifications(anyList(), any());
        }
    }
}
