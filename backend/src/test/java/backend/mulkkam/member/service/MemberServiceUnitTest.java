package backend.mulkkam.member.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberServiceUnitTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("멤버를 조회할 때")
    @Nested
    class Get {

        @DisplayName("존재하는 ID로 조회 시 멤버 정보를 반환한다")
        @Test
        void success_whenExistingId() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();

            // when
            MemberResponse result = memberService.get(member);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.nickname()).isEqualTo(member.getMemberNickname().value());
                softly.assertThat(result.weight()).isEqualTo(member.getPhysicalAttributes().getWeight());
                softly.assertThat(result.gender()).isEqualTo(member.getPhysicalAttributes().getGender().name());
                softly.assertThat(result.targetAmount()).isEqualTo(member.getTargetAmount().value());
            });
        }
    }

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

            Gender gender = Gender.FEMALE;
            Double weight = 50.2;
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest = new PhysicalAttributesModifyRequest(
                    gender,
                    weight
            );

            // when
            memberService.modifyPhysicalAttributes(
                    physicalAttributesModifyRequest,
                    member
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
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("msv0b"))
                    .build();

            String modifyNickname = "msv0a";
            MemberNicknameModifyRequest memberNicknameModifyRequest = new MemberNicknameModifyRequest(
                    modifyNickname);

            // when
            memberService.modifyNickname(
                    memberNicknameModifyRequest,
                    member
            );

            // then
            assertThat(member.getMemberNickname().value()).isEqualTo(modifyNickname);
        }
    }

    @DisplayName("멤버의 닉네임을 수정할 때")
    @Nested
    class modifyNickname {

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
            when(memberRepository.existsByMemberNicknameValue(newNickname)).thenReturn(false);

            // when & then
            assertThatCode(() -> memberService.validateDuplicateNickname(
                    newNickname,
                    member
            )).doesNotThrowAnyException();
        }

        @DisplayName("이미 존재하는 닉네임이면 예외가 발생한다")
        @Test
        void error_duplicateNickname() {
            // given
            String oldNickname = "체체";
            String newNickname = "체체1";

            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname(oldNickname))
                    .build();

            when(memberRepository.existsByMemberNicknameValue(newNickname)).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.validateDuplicateNickname(
                    newNickname,
                    member
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

            // when & then
            assertThatThrownBy(() -> memberService.validateDuplicateNickname(
                    nickname,
                    member
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

            String expected = member.getMemberNickname().value();

            // when
            MemberNicknameResponse memberNicknameResponse = memberService.getNickname(member);

            // then
            assertThat(memberNicknameResponse.memberNickname()).isEqualTo(expected);
        }
    }
}
