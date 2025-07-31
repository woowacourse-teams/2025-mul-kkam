package backend.mulkkam.member.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

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
}
