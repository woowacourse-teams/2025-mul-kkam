package backend.mulkkam.member.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.dto.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixture;
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
    class modifyPhysicalAttributes {

        @DisplayName("올바른 데이터로 모든 필드를 수정할 시 값이 반영된다")
        @Test
        void success_validDataAllArgs() {
            // given
             Member member = new MemberFixture()
                     .weight(null)
                     .gender(null)
                     .build();
            memberRepository.save(member);

            Integer weight = 50;
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
            Member result = memberRepository.findById(member.getId()).get();

            assertSoftly(softly -> {
                softly.assertThat(result.getGender()).isEqualTo(gender);
                softly.assertThat(result.getWeight()).isEqualTo(weight);
            });
        }

        @DisplayName("수정 요청한 필드들의 값들만 반영된다")
        @Test
        void success_validDataSomeArgs() {
            // given
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            Integer weight = 50;
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest = new PhysicalAttributesModifyRequest(
                    null,
                    weight
            );

            // when
            memberService.modifyPhysicalAttributes(
                    physicalAttributesModifyRequest,
                    member.getId()
            );

            // then
            Member result = memberRepository.findById(member.getId()).get();

            assertSoftly(softly -> {
                softly.assertThat(result.getGender()).isEqualTo(member.getGender());
                softly.assertThat(result.getWeight()).isEqualTo(weight);
            });
        }
    }
}
