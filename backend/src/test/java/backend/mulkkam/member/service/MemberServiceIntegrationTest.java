package backend.mulkkam.member.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_MEMBER_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.dto.response.ProgressInfoResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private IntakeHistoryDetailRepository intakeDetailRepository;

    @DisplayName("멤버를 조회할 때")
    @Nested
    class Get {

        @DisplayName("존재하는 ID로 조회 시 멤버 정보를 반환한다")
        @Test
        void success_whenExistingId() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            Member savedMember = memberRepository.save(member);

            // when
            MemberResponse result = memberService.getMemberById(savedMember.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.id()).isEqualTo(savedMember.getId());
                softly.assertThat(result.nickname()).isEqualTo(member.getMemberNickname().value());
                softly.assertThat(result.weight()).isEqualTo(member.getPhysicalAttributes().getWeight());
                softly.assertThat(result.gender()).isEqualTo(member.getPhysicalAttributes().getGender().name());
                softly.assertThat(result.targetAmount()).isEqualTo(member.getTargetAmount().value());
            });
        }

        @DisplayName("존재하지 않는 멤버 id로 조회 시 예외가 발생한다 : NOT_FOUND_MEMBER")
        @Test
        void error_whenNonExistingId() {
            // when & then
            assertThatThrownBy(
                    () -> memberService.getMemberById(Integer.MAX_VALUE)
            ).isInstanceOf(CommonException.class).hasMessage(NOT_FOUND_MEMBER.name());
        }
    }

    @DisplayName("멤버의 신체적인 속성 값을 수정할 때")
    @Nested
    class ModifyPhysicalAttributes {

        @DisplayName("올바른 데이터로 필드를 수정할 시 값이 반영된다")
        @Test
        void success_validDataAllArgs() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
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

    @DisplayName("멤버의 닉네임을 수정하려고 할 때에")
    @Nested
    class ModifyNickname {

        @DisplayName("올바른 닉네임으로 필드를 수정할 시 값이 변경된다")
        @Test
        void success_validNickname() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("msv0b"))
                    .build();
            memberRepository.save(member);

            String modifyNickname = "msv0a";
            MemberNicknameModifyRequest memberNicknameModifyRequest = new MemberNicknameModifyRequest(modifyNickname);

            // when
            memberService.modifyNickname(
                    memberNicknameModifyRequest,
                    member.getId()
            );

            // then
            Member result = memberRepository.findById(member.getId()).orElseThrow();

            assertThat(result.getMemberNickname().value()).isEqualTo(modifyNickname);
        }

        @DisplayName("중복되지 않거나, 기존의 닉네임과 같지 않다면 정상적으로 작동한다")
        @Test
        void success_validDataArg() {
            // given
            String oldNickname = "체체";
            String newNickname = "체체1";
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname(oldNickname))
                    .build();
            memberRepository.save(member);

            // when & then
            assertThatCode(() -> memberService.validateDuplicateNickname(
                    newNickname,
                    member.getId()
            )).doesNotThrowAnyException();
        }

        @DisplayName("이미 존재하는 닉네임이면 예외가 발생한다")
        @Test
        void error_duplicateNickname() {
            // given
            String oldNickname = "체체";
            String newNickname = "체체1";

            Member member1 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname(oldNickname))
                    .build();
            memberRepository.save(member1);

            Member member2 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname(newNickname))
                    .build();
            memberRepository.save(member2);

            // when & then
            assertThatThrownBy(() -> memberService.validateDuplicateNickname(
                    newNickname,
                    member1.getId()
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(DUPLICATE_MEMBER_NICKNAME.name());
        }

        @DisplayName("이전과 같은 닉네임이면 예외가 발생한다")
        @Test
        void error_sameAsBeforeNickname() {
            // given
            String nickname = "체체";
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname(nickname))
                    .build();
            memberRepository.save(member);

            // when & then
            assertThatThrownBy(() -> memberService.validateDuplicateNickname(
                    nickname,
                    member.getId()
            ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(SAME_AS_BEFORE_NICKNAME.name());
        }
    }

    @DisplayName("멤버의 닉네임을 조회하려고 할 때")
    @Nested
    class GetNickname {

        @DisplayName("멤버의 닉네임이 올바르게 조회된다")
        @Test
        void success_validMemberId() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            memberRepository.save(member);

            String expected = member.getMemberNickname().value();

            // when
            MemberNicknameResponse memberNicknameResponse = memberService.getNickname(member.getId());

            // then
            assertThat(memberNicknameResponse.memberNickname()).isEqualTo(expected);
        }
    }

    @DisplayName("온보딩 시에")
    @Nested
    class Create {

        @DisplayName("정상적으로 회원이 저장된다")
        @Test
        void success_validData() {
            // given
            String rawNickname = "히로";
            double weight = 60.0;
            Gender gender = Gender.FEMALE;
            int rawTargetIntakeAmount = 1_000;
            CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                    rawNickname,
                    weight,
                    gender,
                    rawTargetIntakeAmount
            );

            // when
            memberService.create(createMemberRequest);

            // then
            List<Member> savedMembers = memberRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(savedMembers.size()).isEqualTo(1);
                softly.assertThat(savedMembers.getFirst().getMemberNickname())
                        .isEqualTo(new MemberNickname(rawNickname));
                softly.assertThat(savedMembers.getFirst().getPhysicalAttributes().getWeight()).isEqualTo(weight);
                softly.assertThat(savedMembers.getFirst().getPhysicalAttributes().getGender()).isEqualTo(gender);
                softly.assertThat(savedMembers.getFirst().getTargetAmount())
                        .isEqualTo(new Amount(rawTargetIntakeAmount));
            });
        }

        @DisplayName("유효하지 않은 닉네임을 사용할 경우 예외를 반환한다")
        @ParameterizedTest
        @ValueSource(strings = {"1", " ", "", "1234567891011"})
        void error_invalidNickname(String invalidNickname) {
            // given
            double weight = 60.0;
            Gender gender = Gender.FEMALE;
            int rawTargetIntakeAmount = 1_000;
            CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                    invalidNickname,
                    weight,
                    gender,
                    rawTargetIntakeAmount
            );

            // when & then
            assertThatThrownBy(() -> memberService.create(createMemberRequest))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_MEMBER_NICKNAME.name());
        }

    }

    @DisplayName("유효하지 않은 목표 음용량을 사용할 경우 예외를 반환한다")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -3})
    void error_invalidTargetAmount(int invalidIntakeAmount) {
        // given
        String rawNickname = "히로";
        double weight = 60.0;
        Gender gender = Gender.FEMALE;
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                rawNickname,
                weight,
                gender,
                invalidIntakeAmount
        );

        // when & then
        assertThatThrownBy(() -> memberService.create(createMemberRequest))
                .isInstanceOf(CommonException.class)
                .hasMessage(INVALID_AMOUNT.name());
    }

    @DisplayName("멤버의 진행 상황을 조회할 때")
    @Nested
    class GetProgressInfo {

        @DisplayName("정상적으로 작동한다")
        @Test
        void success_validData() {
            // given
            String nickname = "체체";
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname(nickname))
                    .build();
            memberRepository.save(member);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetIntakeAmount(new Amount(1000))
                    .date(LocalDate.of(2025, 7, 15))
                    .streak(42)
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new Amount(500))
                    .build();
            intakeDetailRepository.save(intakeHistoryDetail);

            // when
            ProgressInfoResponse progressInfoResponse = memberService.getProgressInfo(
                    LocalDate.of(2025, 7, 15),
                    member.getId()
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(progressInfoResponse.memberNickname()).isEqualTo(nickname);
                softly.assertThat(progressInfoResponse.streak()).isEqualTo(42);
                softly.assertThat(progressInfoResponse.achievementRate()).isEqualTo(50.0);
                softly.assertThat(progressInfoResponse.targetAmount()).isEqualTo(1000);
                softly.assertThat(progressInfoResponse.totalAmount()).isEqualTo(500);
            });
        }
    }
}
