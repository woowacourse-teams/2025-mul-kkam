package backend.mulkkam.member.service;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.dto.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceUnitTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("멤버의 신체적인 속성 값을 수정할 때")
    @Nested
    class modifyPhysicalAttributes {

        @DisplayName("올바른 데이터로 모든 필드를 수정할 시 값이 반영된다")
        @Test
        void success_validDataAllArgs() {
            // given
            Member member = MemberFixture.builder()
                    .weight(null)
                    .gender(null)
                    .build();
            Long memberId = 1L;
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            Gender gender = Gender.FEMALE;
            Double weight = 50.2;
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest = new PhysicalAttributesModifyRequest(
                    gender,
                    weight
            );

            // when
            memberService.modifyPhysicalAttributes(
                    physicalAttributesModifyRequest,
                    memberId
            );

            // then
            Member result = memberRepository.findById(memberId).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(result.getMemberNickname()).isEqualTo(member.getMemberNickname());
                softly.assertThat(result.getPhysicalAttributes().getGender()).isEqualTo(gender);
                softly.assertThat(result.getPhysicalAttributes().getWeight()).isEqualTo(weight);
                softly.assertThat(result.getTargetAmount()).isEqualTo(member.getTargetAmount());
            });
        }
    }
}
