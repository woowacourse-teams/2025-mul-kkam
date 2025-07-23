package backend.mulkkam.cup.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixture;
import backend.mulkkam.support.MemberFixture;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CupServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private CupService cupService;

    @Autowired
    private CupRepository cupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("컵을 생성할 때에")
    @Nested
    class Create {

        @DisplayName("정상적으로 저장한다")
        @Test
        void success() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스",
                    500
            );
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            // when
            CupResponse cupResponse = cupService.create(
                    cupRegisterRequest,
                    member.getId()
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupResponse.nickname()).isEqualTo("스타벅스");
                softly.assertThat(cupResponse.amount()).isEqualTo(500);
                softly.assertThat(cupRepository.findById(cupResponse.id())).isPresent();
            });
        }

        @DisplayName("용량이 음수면 예외가 발생한다")
        @Test
        void error_amountLessThan0() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest("스타벅스", -100);
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            // when & then
            assertThatThrownBy(() -> cupService.create(cupRegisterRequest, member.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("컵이 3개 저장되어 있을 때 예외가 발생한다")
        @Test
        void error_memberAlreadyHasThreeCups() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스",
                    500
            );
            Member member = new MemberFixture().build();
            memberRepository.save(member);
            CupRegisterRequest cupRegisterRequest1 = new CupRegisterRequest(
                    "스타벅스",
                    500
            );
            CupRegisterRequest cupRegisterRequest2 = new CupRegisterRequest(
                    "스타벅스",
                    500
            );
            CupRegisterRequest cupRegisterRequest3 = new CupRegisterRequest(
                    "스타벅스",
                    500
            );

            // when
            cupService.create(
                    cupRegisterRequest1,
                    member.getId()
            );
            cupService.create(
                    cupRegisterRequest2,
                    member.getId()
            );
            cupService.create(
                    cupRegisterRequest3,
                    member.getId()
            );

            // then
            assertThatThrownBy(() -> cupService.create(cupRegisterRequest, member.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("컵을 읽을 때에")
    @Nested
    class read {

        @DisplayName("사용자의 컵을 모두 가져온다")
        @Test
        void success_readAllCupsFromMember() {
            // given
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            Cup cup1 = new CupFixture()
                    .member(member)
                    .cupRank(new CupRank(1))
                    .build();

            Cup cup2 = new CupFixture()
                    .member(member)
                    .cupRank(new CupRank(2))
                    .build();
            List<Cup> cups = List.of(cup1, cup2);
            cupRepository.saveAll(cups);

            // when
            CupsResponse cupsResponse = cupService.readCupsByMemberId(member.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupsResponse.size()).isEqualTo(2);
                softly.assertThat(cupsResponse.cups().getFirst().nickname()).isEqualTo(cup1.getNickname().value());
                softly.assertThat(cupsResponse.cups().getFirst().amount()).isEqualTo(cup1.getCupAmount().value());
            });
        }
    }
}
