package backend.mulkkam.notification.controller;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_REMINDER_SCHEDULE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_REMINDER_SCHEDULE;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.ReminderSchedule;
import backend.mulkkam.notification.dto.request.CreateReminderScheduleRequest;
import backend.mulkkam.notification.dto.request.ModifyReminderScheduleTimeRequest;
import backend.mulkkam.notification.dto.response.ReadReminderSchedulesResponse;
import backend.mulkkam.notification.repository.ReminderScheduleRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.ReminderScheduleFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class ReminderScheduleControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private ReminderScheduleRepository reminderScheduleRepository;

    private Member savedMember;

    private String token;

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder
                .builder().build();
        savedMember = memberRepository.save(member);

        OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);
        String deviceUuid = "deviceUuid";
        token = oauthJwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);
    }

    @DisplayName("리마인더 스케줄링을 조회할 때")
    @Nested
    class Read {

        @BeforeEach
        void setUp() {
            ReminderSchedule reminderSchedule1 = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(15, 30))
                    .build();
            ReminderSchedule reminderSchedule2 = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(12, 30))
                    .build();

            reminderScheduleRepository.saveAll(List.of(reminderSchedule1, reminderSchedule2));
        }

        @DisplayName("사용자의 모든 스케쥴링을 보여준다.")
        @Test
        void success_whenReadOrderByAsc() throws Exception {
            // when
            String json = mockMvc.perform(get("/reminder")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // then
            ReadReminderSchedulesResponse actual = objectMapper.readValue(json, ReadReminderSchedulesResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.isReminderEnabled()).isTrue();
                softly.assertThat(actual.reminderSchedules().size()).isEqualTo(2);
                softly.assertThat(actual.reminderSchedules().getFirst().schedule()).isEqualTo(LocalTime.of(12, 30));
            });
        }

        @DisplayName("사용자의 모든 스케쥴링이 정렬되어서 반환된다.")
        @Test
        void success_whenRead() throws Exception {
            // when
            String json = mockMvc.perform(get("/reminder")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // then
            ReadReminderSchedulesResponse actual = objectMapper.readValue(json, ReadReminderSchedulesResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.isReminderEnabled()).isTrue();
                softly.assertThat(actual.reminderSchedules().size()).isEqualTo(2);
                softly.assertThat(actual.reminderSchedules().getFirst().schedule()).isEqualTo(LocalTime.of(12, 30));
            });
        }
    }

    @DisplayName("리마인더 스케줄링을 추가할 때")
    @Nested
    class Create {

        @BeforeEach
        void setUp() {
            ReminderSchedule reminderSchedule1 = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(12, 30))
                    .build();
            ReminderSchedule reminderSchedule2 = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(15, 30))
                    .build();

            reminderScheduleRepository.saveAll(List.of(reminderSchedule1, reminderSchedule2));
        }

        @DisplayName("올바르게 스케줄링을 추가한다.")
        @Test
        void success_whenCreate() throws Exception {
            //given
            CreateReminderScheduleRequest createReminderScheduleRequest = new CreateReminderScheduleRequest(
                    LocalTime.of(16, 0));

            // when
            mockMvc.perform(post("/reminder")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createReminderScheduleRequest)))
                    .andExpect(status().isOk());
            Member foundMember = memberRepository.findById(savedMember.getId()).orElseThrow();
            List<ReminderSchedule> reminderSchedules = reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(
                    foundMember);
            //then
            assertSoftly(softly -> {
                softly.assertThat(reminderSchedules.size()).isEqualTo(3);
            });
        }

        @DisplayName("기존에 있는 시간일 경우 예외가 발생한다.")
        @Test
        void error_whenHaveToSameTime() throws Exception {
            //given
            CreateReminderScheduleRequest createReminderScheduleRequest = new CreateReminderScheduleRequest(
                    LocalTime.of(12, 30));

            // when
            String json = mockMvc.perform(post("/reminder")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createReminderScheduleRequest)))
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            //then
            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(DUPLICATED_REMINDER_SCHEDULE.name());
            });
        }
    }

    @DisplayName("리마인더 스케줄링을 수정할 때")
    @Nested
    class Modify {

        @BeforeEach
        void setUp() {
            ReminderSchedule reminderSchedule1 = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(12, 30))
                    .build();
            ReminderSchedule reminderSchedule2 = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(15, 30))
                    .build();

            reminderScheduleRepository.saveAll(List.of(reminderSchedule1, reminderSchedule2));
        }

        @DisplayName("올바르게 스케줄링을 수정한다.")
        @Test
        void success_whenModify() throws Exception {
            //given
            ReminderSchedule reminderSchedule3 = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(19, 30))
                    .build();
            ReminderSchedule savedReminderSchedule = reminderScheduleRepository.save(reminderSchedule3);

            ModifyReminderScheduleTimeRequest modifyReminderScheduleTimeRequest = new ModifyReminderScheduleTimeRequest
                    (
                            savedReminderSchedule.getId(),
                            LocalTime.of(16, 0)
                    );

            // when
            mockMvc.perform(patch("/reminder")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyReminderScheduleTimeRequest)))
                    .andExpect(status().isOk());
            Member foundMember = memberRepository.findById(savedMember.getId()).orElseThrow();
            List<ReminderSchedule> reminderSchedules = reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(
                    foundMember);
            //then
            assertSoftly(softly -> {
                softly.assertThat(reminderSchedules.size()).isEqualTo(3);
            });
        }

        @DisplayName("기존에 있는 시간일 경우 예외가 발생한다.")
        @Test
        void error_whenHaveToSameTime() throws Exception {
            //given
            ReminderSchedule reminderSchedule3 = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(19, 30))
                    .build();
            ReminderSchedule savedReminderSchedule = reminderScheduleRepository.save(reminderSchedule3);

            ModifyReminderScheduleTimeRequest modifyReminderScheduleTimeRequest = new ModifyReminderScheduleTimeRequest
                    (
                            savedReminderSchedule.getId(),
                            LocalTime.of(12, 30)
                    );

            // when
            String json = mockMvc.perform(patch("/reminder")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyReminderScheduleTimeRequest)))
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            //then
            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(DUPLICATED_REMINDER_SCHEDULE.name());
            });
        }
    }

    @DisplayName("리마인더 스케줄링을 삭제할 때")
    @Nested
    class Delete {

        @DisplayName("올바르게 스케줄링을 삭제한다.")
        @Test
        void success_whenDelete() throws Exception {
            //given
            ReminderSchedule reminderSchedule = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(19, 30))
                    .build();
            ReminderSchedule savedReminderSchedule = reminderScheduleRepository.save(reminderSchedule);

            // when
            mockMvc.perform(delete("/reminder/{id}", savedReminderSchedule.getId())
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            Member foundMember = memberRepository.findById(savedMember.getId()).orElseThrow();
            List<ReminderSchedule> reminderSchedules = reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(
                    foundMember);
            //then
            assertSoftly(softly -> {
                softly.assertThat(reminderSchedules.size()).isEqualTo(0);
            });
        }

        @DisplayName("없는 id일 경우 예외가 발생한다.")
        @Test
        void error_whenDeleteNotFound() throws Exception {
            // when
            String json = mockMvc.perform(delete("/reminder/{id}", Long.MAX_VALUE)
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            //then
            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_FOUND_REMINDER_SCHEDULE.name());
            });
        }

        @DisplayName("생성, 삭제, 생성, 삭제 순으로 해도 올바르게 저장된다.")
        @Test
        void success_whenExistDuplicateDeleteRow() throws Exception {
            //given
            ReminderSchedule reminderSchedule = ReminderScheduleFixtureBuilder
                    .withMember(savedMember)
                    .schedule(LocalTime.of(19, 30))
                    .build();
            ReminderSchedule savedReminderSchedule = reminderScheduleRepository.save(reminderSchedule);

            // when
            mockMvc.perform(delete("/reminder/{id}", savedReminderSchedule.getId())
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());
            CreateReminderScheduleRequest createReminderScheduleRequest = new CreateReminderScheduleRequest(
                    LocalTime.of(19, 30));

            mockMvc.perform(post("/reminder")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createReminderScheduleRequest)))
                    .andExpect(status().isOk());

            List<ReminderSchedule> pastReminderSchedules = reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(
                    savedMember);

            mockMvc.perform(delete("/reminder/{id}", pastReminderSchedules.getFirst().getId())
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());
            List<ReminderSchedule> currentReminderSchedules = reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(
                    savedMember);

            //then
            assertSoftly(softly -> {
                softly.assertThat(currentReminderSchedules.size()).isEqualTo(0);
            });
        }
    }
}
