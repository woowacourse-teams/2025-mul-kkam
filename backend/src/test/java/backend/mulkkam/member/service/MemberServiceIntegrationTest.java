package backend.mulkkam.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.ProgressInfoResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @DisplayName("멤버의 신체적인 속성 값을 수정할 때")
    @Nested
    class modifyPhysicalAttributes {

        @DisplayName("올바른 데이터로 필드를 수정할 시 값이 반영된다")
        @Test
        void success_validDataAllArgs() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .weight(null)
                    .gender(null)
                    .build();
            memberRepository.save(member);

            Double weight = 50.2;
            Gender gender = Gender.FEMALE;
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest = new PhysicalAttributesModifyRequest(
                    gender,
                    weight
            );

            // when
            memberService.modifyPhysicalAttributes(
                    physicalAttributesModifyRequest,
                    member.getId()
            );

            // then
            Member result = memberRepository.findById(member.getId()).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(result.getMemberNickname()).isEqualTo(member.getMemberNickname());
                softly.assertThat(result.getPhysicalAttributes().getGender()).isEqualTo(gender);
                softly.assertThat(result.getPhysicalAttributes().getWeight()).isEqualTo(weight);
                softly.assertThat(result.getTargetAmount()).isEqualTo(member.getTargetAmount());
            });
        }
    }

    @DisplayName("멤버의 금일 활동 정보를 가져올 때")
    @Nested
    class Get {

        @DisplayName("정상적으로 가져온다")
        @Test
        void success_validDataAllArgs() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            memberRepository.save(member);

            IntakeHistory intakeHistory1 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .dateTime(LocalDateTime.of(2025, 7, 25, 15, 5))
                    .build();
            IntakeHistory intakeHistory2 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .dateTime(LocalDateTime.of(2025, 7, 25, 15, 30))
                    .build();
            IntakeHistory intakeHistory3 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .dateTime(LocalDateTime.of(2025, 7, 24, 15, 5))
                    .build();
            IntakeHistory intakeHistory4 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .dateTime(LocalDateTime.of(2025, 7, 23, 15, 5))
                    .build();
            IntakeHistory intakeHistory5 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .dateTime(LocalDateTime.of(2025, 7, 21, 15, 5))
                    .build();

            intakeHistoryRepository.saveAll(List.of(
                    intakeHistory1,
                    intakeHistory2,
                    intakeHistory3,
                    intakeHistory4,
                    intakeHistory5
            ));

            DateRangeRequest dateRangeRequest = new DateRangeRequest(
                    LocalDate.of(2025, 7, 25),
                    LocalDate.of(2025, 7, 25)
            );

            ProgressInfoResponse progressInfoResponse = memberService.getTodayProgressInfo(
                    dateRangeRequest,
                    member.getId()
            );

            assertSoftly(
                    softAssertions -> assertThat(progressInfoResponse.streak()).isEqualTo(3)
            );
        }
    }
}
