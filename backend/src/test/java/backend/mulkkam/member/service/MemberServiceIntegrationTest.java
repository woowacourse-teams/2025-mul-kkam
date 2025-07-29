package backend.mulkkam.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
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
            memberService.modifyNickname(memberNicknameModifyRequest, member.getId());

            // then
            Member result = memberRepository.findById(member.getId()).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(result.getMemberNickname().value()).isEqualTo(modifyNickname);
                softly.assertThat(result.getPhysicalAttributes().getGender()).isEqualTo(member.getPhysicalAttributes().getGender());
                softly.assertThat(result.getPhysicalAttributes().getWeight()).isEqualTo(member.getPhysicalAttributes().getWeight());
                softly.assertThat(result.getTargetAmount()).isEqualTo(member.getTargetAmount());
            });
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
