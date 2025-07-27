package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_SIZE;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.request.CupNicknameAndAmountModifyRequest;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
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
        void success_validData() {
            // given
            String cupNickname = "스타벅스";
            Integer cupAmount = 500;
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    cupNickname,
                    cupAmount
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
                softly.assertThat(cupResponse.cupNickname()).isEqualTo(cupNickname);
                softly.assertThat(cupResponse.cupAmount()).isEqualTo(cupAmount);
                softly.assertThat(cupRepository.findById(cupResponse.id())).isPresent();
            });
        }

        @DisplayName("용량이 음수면 예외가 발생한다")
        @Test
        void error_amountLessThan0() {
            // given
            String cupNickname = "스타벅스";
            Integer cupAmount = -100;
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    cupNickname,
                    cupAmount
            );
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
        }

        @DisplayName("용량이 0이면 예외가 발생한다")
        @Test
        void error_amountIsEqualTo0() {
            // given
            String cupNickname = "스타벅스";
            Integer cupAmount = 0;
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(cupNickname, cupAmount);
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
        }

        @DisplayName("컵이 3개 저장되어 있을 때 예외가 발생한다")
        @Test
        void error_memberAlreadyHasThreeCups() {
            // given
            CupRegisterRequest cupRegisterRequest = new CupRegisterRequest(
                    "스타벅스1",
                    500
            );
            Member member = new MemberFixture().build();
            memberRepository.save(member);
            CupRegisterRequest cupRegisterRequest1 = new CupRegisterRequest(
                    "스타벅스2",
                    500
            );
            CupRegisterRequest cupRegisterRequest2 = new CupRegisterRequest(
                    "스타벅스3",
                    500
            );
            CupRegisterRequest cupRegisterRequest3 = new CupRegisterRequest(
                    "스타벅스4",
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
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.create(cupRegisterRequest, member.getId()));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_SIZE);
        }
    }

    @DisplayName("컵을 읽을 때에")
    @Nested
    class Read {

        @DisplayName("사용자의 컵을 랭크순으로 모두 가져온다")
        @Test
        void success_withExistedMemberId() {
            // given
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            Cup cup1 = new CupFixture()
                    .member(member)
                    .cupRank(new CupRank(2))
                    .build();

            Cup cup2 = new CupFixture()
                    .member(member)
                    .cupRank(new CupRank(1))
                    .build();
            List<Cup> cups = List.of(cup1, cup2);
            cupRepository.saveAll(cups);

            // when
            CupsResponse cupsResponse = cupService.readCupsByMemberId(member.getId());

            CupResponse firstCup = cupsResponse.cups().getFirst();
            CupResponse secondCup = cupsResponse.cups().get(1);

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupsResponse.size()).isEqualTo(2);
                softly.assertThat(firstCup.cupNickname()).isEqualTo(cup2.getNickname().value());
                softly.assertThat(firstCup.cupAmount()).isEqualTo(cup2.getCupAmount().value());
                softly.assertThat(firstCup.cupRank()).isEqualTo(cup2.getCupRank().value());
                softly.assertThat(secondCup.cupRank()).isEqualTo(cup1.getCupRank().value());
                List<Integer> ranks = cupsResponse.cups().stream()
                        .map(CupResponse::cupRank)
                        .toList();
                softly.assertThat(ranks).isSorted();
            });
        }
    }

    @DisplayName("컵을 수정할 때에")
    @Nested
    class Modify {

        @DisplayName("컵 이름 및 용량이 수정된다")
        @Test
        void success_withValidData() {
            // given
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            String beforeCupNickName = "변경 전";
            String afterCupNickName = "변경 후";
            Integer beforeCupAmount = 500;
            Integer afterCupAmount = 1000;

            Cup cup = new CupFixture()
                    .member(member)
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .build();

            Cup savedCup = cupRepository.save(cup);
            CupNicknameAndAmountModifyRequest cupNicknameAndAmountModifyRequest = new CupNicknameAndAmountModifyRequest(
                    afterCupNickName,
                    afterCupAmount
            );

            // when
            cupService.modifyNicknameAndAmount(
                    savedCup.getId(),
                    member.getId(),
                    cupNicknameAndAmountModifyRequest
            );

            Cup changedCup = cupRepository.findById(savedCup.getId())
                    .orElseThrow();

            // then
            assertSoftly(softly -> {
                softly.assertThat(changedCup.getCupAmount().value()).isEqualTo(afterCupAmount);
                softly.assertThat(changedCup.getNickname().value()).isEqualTo(afterCupNickName);
            });
        }

        @DisplayName("수정할 컵만 변경된다")
        @Test
        void success_onlyOneCupShouldBeModified() {
            // given
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            String beforeCupNickName1 = "변경 전1";
            String beforeCupNickName2 = "변경 전2";
            String afterCupNickName = "변경 후";
            Integer beforeCupAmount1 = 300;
            Integer beforeCupAmount2 = 500;
            Integer afterCupAmount = 1000;

            Cup cup1 = new CupFixture()
                    .member(member)
                    .cupNickname(new CupNickname(beforeCupNickName1))
                    .cupAmount(new CupAmount(beforeCupAmount1))
                    .build();

            Cup cup2 = new CupFixture()
                    .member(member)
                    .cupNickname(new CupNickname(beforeCupNickName2))
                    .cupAmount(new CupAmount(beforeCupAmount2))
                    .build();

            cupRepository.saveAll(List.of(
                    cup1,
                    cup2
            ));

            CupNicknameAndAmountModifyRequest cupNicknameAndAmountModifyRequest = new CupNicknameAndAmountModifyRequest(
                    afterCupNickName,
                    afterCupAmount
            );

            // when
            cupService.modifyNicknameAndAmount(
                    cup1.getId(),
                    member.getId(),
                    cupNicknameAndAmountModifyRequest
            );

            Cup changedCup1 = cupRepository.findById(cup1.getId())
                    .orElseThrow();
            Cup changedCup2 = cupRepository.findById(cup2.getId())
                    .orElseThrow();

            // then
            assertSoftly(softly -> {
                softly.assertThat(changedCup1.getNickname().value()).isEqualTo(afterCupNickName);
                softly.assertThat(changedCup1.getCupAmount().value()).isEqualTo(afterCupAmount);
                softly.assertThat(changedCup2.getNickname().value()).isEqualTo(beforeCupNickName2);
                softly.assertThat(changedCup2.getCupAmount().value()).isEqualTo(beforeCupAmount2);
            });
        }

        @DisplayName("멤버가 다를 경우 예외가 발생한다")
        @Test
        void error_ifTheMembersAreDifferent() {
            // given
            Member member1 = new MemberFixture()
                    .memberNickname(new MemberNickname("멤버1"))
                    .build();
            Member member2 = new MemberFixture()
                    .memberNickname(new MemberNickname("멤버2"))
                    .build();

            memberRepository.saveAll(List.of(member1, member2));

            String beforeCupNickName = "변경 전";
            String afterCupNickName = "변경 후";
            Integer beforeCupAmount = 500;
            Integer afterCupAmount = 1000;

            Cup cup = new CupFixture()
                    .member(member1)
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .build();

            cupRepository.save(cup);

            CupNicknameAndAmountModifyRequest cupNicknameAndAmountModifyRequest = new CupNicknameAndAmountModifyRequest(
                    afterCupNickName,
                    afterCupAmount
            );

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.modifyNicknameAndAmount(cup.getId(), member2.getId(),
                            cupNicknameAndAmountModifyRequest));
            assertThat(ex.getErrorCode()).isEqualTo(NOT_PERMITTED_FOR_CUP);
        }
    }

}
