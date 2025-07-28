package backend.mulkkam.member.service;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
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
            Member member = MemberFixtureBuilder.builder()
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
            assertSoftly(softly -> {
                softly.assertThat(member.getMemberNickname()).isEqualTo(member.getMemberNickname());
                softly.assertThat(member.getPhysicalAttributes().getGender()).isEqualTo(gender);
                softly.assertThat(member.getPhysicalAttributes().getWeight()).isEqualTo(weight);
                softly.assertThat(member.getTargetAmount()).isEqualTo(member.getTargetAmount());
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
            Member member = new MemberFixture()
                    .memberNickname(new MemberNickname("msv0b"))
                    .build();
            Long memberId = 1L;
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            String modifyNickname = "msv0a";
            MemberNicknameModifyRequest memberNicknameModifyRequest = new MemberNicknameModifyRequest(modifyNickname);

            // when
            memberService.modifyNickname(
                    memberNicknameModifyRequest,
                    memberId
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(member.getMemberNickname().value()).isEqualTo(modifyNickname);
                softly.assertThat(member.getPhysicalAttributes().getGender()).isEqualTo(member.getPhysicalAttributes().getGender());
                softly.assertThat(member.getPhysicalAttributes().getWeight()).isEqualTo(member.getPhysicalAttributes().getWeight());
                softly.assertThat(member.getTargetAmount()).isEqualTo(member.getTargetAmount());
            });
        }
    }
}
