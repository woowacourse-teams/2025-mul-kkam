package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP_RANKS;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.FORBIDDEN;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.CupRankDto;
import backend.mulkkam.cup.dto.request.CupNicknameAndAmountModifyRequest;
import backend.mulkkam.cup.dto.request.CupRegisterRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

        private final Member member = MemberFixtureBuilder.builder().build();

        @BeforeEach
        void setup() {
            memberRepository.save(member);
        }

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

        @DisplayName("세 개의 컵이 삭제되었다가 추가되는 경우, 우선순위가 중복되지 않는다.")
        @Test
        void success_createAfterDeleted() {
            // given
            Cup firstCup = new Cup(member, new CupNickname("first"), new CupAmount(100), new CupRank(1));
            Cup secondCup = new Cup(member, new CupNickname("second"), new CupAmount(100), new CupRank(2));
            Cup thirdCup = new Cup(member, new CupNickname("third"), new CupAmount(100), new CupRank(3));

            cupRepository.save(firstCup);
            cupRepository.save(secondCup);
            cupRepository.save(thirdCup);

            cupService.delete(thirdCup.getId(), member.getId());

            CupRegisterRequest request = new CupRegisterRequest("new", 100);

            // when
            cupService.create(request, member.getId());

            // then
            assertSoftly(softly -> {
                List<Cup> cups = cupRepository.findAllByMemberId(member.getId());
                softly.assertThat(cups).hasSize(3);
                softly.assertThat(cups.stream().map(Cup::getCupRank)).doesNotHaveDuplicates();
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
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_COUNT);
        }
    }

    @DisplayName("컵을 읽을 때에")
    @Nested
    class Read {

        @DisplayName("사용자의 컵을 랭크순으로 모두 가져온다")
        @Test
        void success_withExistedMemberId() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
            memberRepository.save(member);

            Cup cup1 = CupFixtureBuilder
                    .withMember(member)
                    .cupRank(new CupRank(2))
                    .build();

            Cup cup2 = CupFixtureBuilder
                    .withMember(member)
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

    @DisplayName("컵을 삭제할 때")
    @Nested
    class Delete {

        private final Member member = MemberFixtureBuilder.builder().build();
        private final Cup firstCup = CupFixtureBuilder
                .withMember(member)
                .cupRank(new CupRank(1))
                .build();
        private final Cup secondCup = CupFixtureBuilder
                .withMember(member)
                .cupRank(new CupRank(2))
                .build();
        private final Cup thirdCup = CupFixtureBuilder
                .withMember(member)
                .cupRank(new CupRank(3))
                .build();

        @BeforeEach
        void setup() {
            memberRepository.save(member);
            cupRepository.save(firstCup);
            cupRepository.save(secondCup);
            cupRepository.save(thirdCup);
        }

        @DisplayName("우선순위가 더 낮은 컵들의 우선순위가 한 단계씩 승격된다.")
        @Test
        void success_withLowerPriorityCups() {
            // when
            cupService.delete(firstCup.getId(), member.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupRepository.existsById(firstCup.getId())).isFalse();
                softly.assertThat(cupRepository.findAllByMemberId(member.getId())).hasSize(2);
                softly.assertThat(cupRepository.findById(secondCup.getId()))
                        .isPresent()
                        .get()
                        .extracting(Cup::getCupRank)
                        .isEqualTo(new CupRank(1));
                softly.assertThat(cupRepository.findById(thirdCup.getId()))
                        .isPresent()
                        .get()
                        .extracting(Cup::getCupRank)
                        .isEqualTo(new CupRank(2));
            });
        }

        @DisplayName("우선순위가 더 낮은 컵이 없는 경우, 그 어떤 컵의 우선순위도 승격되지 않는다.")
        @Test
        void success_withoutLowerPriorityCups() {
            // when
            cupService.delete(thirdCup.getId(), member.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupRepository.existsById(thirdCup.getId())).isFalse();
                softly.assertThat(cupRepository.findAllByMemberId(member.getId())).hasSize(2);
                softly.assertThat(cupRepository.findById(firstCup.getId()))
                        .isPresent()
                        .get()
                        .extracting(Cup::getCupRank)
                        .isEqualTo(new CupRank(1));
                softly.assertThat(cupRepository.findById(secondCup.getId()))
                        .isPresent()
                        .get()
                        .extracting(Cup::getCupRank)
                        .isEqualTo(new CupRank(2));
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
            Member member = MemberFixtureBuilder.builder().build();
            memberRepository.save(member);

            String beforeCupNickName = "변경 전";
            Integer beforeCupAmount = 500;

            Cup cup = CupFixtureBuilder
                    .withMember(member)
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .build();

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;

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

        @DisplayName("그 멤버의 수정할 컵만 변경된다")
        @Test
        void success_whenCertainCupChanges() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
            memberRepository.save(member);

            String beforeCupNickName1 = "변경 전1";
            Integer beforeCupAmount1 = 300;

            Cup cup1 = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname(beforeCupNickName1))
                    .cupAmount(new CupAmount(beforeCupAmount1))
                    .build();

            String beforeCupNickName2 = "변경 전2";
            Integer beforeCupAmount2 = 500;

            Cup cup2 = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname(beforeCupNickName2))
                    .cupAmount(new CupAmount(beforeCupAmount2))
                    .build();

            cupRepository.saveAll(List.of(
                    cup1,
                    cup2
            ));

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;
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
            Member member1 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("멤버1"))
                    .build();
            Member member2 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("멤버2"))
                    .build();

            memberRepository.saveAll(List.of(member1, member2));

            String beforeCupNickName = "변경 전";

            Integer beforeCupAmount = 500;

            Cup cup = CupFixtureBuilder
                    .withMember(member1)
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .build();

            cupRepository.save(cup);

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;

            CupNicknameAndAmountModifyRequest cupNicknameAndAmountModifyRequest = new CupNicknameAndAmountModifyRequest(
                    afterCupNickName,
                    afterCupAmount
            );

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.modifyNicknameAndAmount(
                            cup.getId(),
                            member2.getId(),
                            cupNicknameAndAmountModifyRequest)
            );
            assertThat(ex.getErrorCode()).isEqualTo(NOT_PERMITTED_FOR_CUP);
        }
    }

    @DisplayName("컵의 우선순위를 변경할 때")
    @Nested
    class UpdateRanks {

        private final Member member = MemberFixtureBuilder.builder().build();

        @BeforeEach
        void setup() {
            memberRepository.save(member);
        }

        @DisplayName("중복되지 않는 식별자 및 우선순위로 자신의 컵을 수정할 수 있다.")
        @Test
        void success_ifModifyMyCups() {
            // given
            Cup firstCup = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname("first"))
                    .cupRank(new CupRank(1))
                    .build();
            Cup secondCup = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname("second"))
                    .cupRank(new CupRank(2))
                    .build();
            Cup thirdCup = CupFixtureBuilder
                    .withMember(member)
                    .cupNickname(new CupNickname("third"))
                    .cupRank(new CupRank(3))
                    .build();

            cupRepository.saveAll(List.of(firstCup, secondCup, thirdCup));

            List<CupRankDto> cupRanks = List.of(
                    new CupRankDto(1L, 3),
                    new CupRankDto(2L, 2),
                    new CupRankDto(3L, 1)
            );
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when & then
            assertSoftly(softly -> {
                softly.assertThatCode(() -> cupService.updateRanks(request, member.getId()))
                        .doesNotThrowAnyException();
                softly.assertThat(cupRepository.findById(firstCup.getId()).get().getCupRank()).isEqualTo(new CupRank(3));
                softly.assertThat(cupRepository.findById(secondCup.getId()).get().getCupRank()).isEqualTo(new CupRank(2));
                softly.assertThat(cupRepository.findById(thirdCup.getId()).get().getCupRank()).isEqualTo(new CupRank(1));
            });
        }

        @DisplayName("요청에 존재하지 않는 컵 식별자가 포함된 경우 예외가 발생한다.")
        @Test
        void error_containsNotExistCupId() {
            // given
            List<CupRankDto> cupRanks = List.of(
                    new CupRankDto(1L, 1)
            );
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when & then
            assertThatThrownBy(() -> cupService.updateRanks(request, member.getId()))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_CUP.name());
        }

        @DisplayName("중복되는 컵 id가 존재하는 경우 예외가 발생한다.")
        @Test
        void error_existsDuplicatedCupIds() {
            // given
            List<CupRankDto> cupRanks = List.of(
                    new CupRankDto(1L, 1),
                    new CupRankDto(1L, 2),
                    new CupRankDto(2L, 3)
            );
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when & then
            assertThatThrownBy(() -> cupService.updateRanks(request, member.getId()))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(DUPLICATED_CUP.name());
        }

        @DisplayName("중복되는 컵 우선순위가 존재하는 경우 예외가 발생한다.")
        @Test
        void error_existsDuplicatedCupRanks() {
            // given
            List<CupRankDto> cupRanks = List.of(
                    new CupRankDto(1L, 1),
                    new CupRankDto(2L, 1),
                    new CupRankDto(3L, 3)
            );
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when & then
            assertThatThrownBy(() -> cupService.updateRanks(request, member.getId()))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(DUPLICATED_CUP_RANKS.name());
        }

        @DisplayName("다른 멤버의 컵을 수정하려는 경우 예외가 발생한다.")
        @Test
        void error_ifModifyOtherMemberCup() {
            // given
            Member me = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("me"))
                    .build();
            Member other = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("other"))
                    .build();

            memberRepository.saveAll(List.of(me, other));

            Cup firstCup = CupFixtureBuilder
                    .withMember(me)
                    .cupNickname(new CupNickname("first"))
                    .build();
            Cup secondCup = CupFixtureBuilder
                    .withMember(other)
                    .cupNickname(new CupNickname("second"))
                    .build();
            Cup thirdCup = CupFixtureBuilder
                    .withMember(other)
                    .cupNickname(new CupNickname("third"))
                    .build();

            cupRepository.saveAll(List.of(firstCup, secondCup, thirdCup));

            List<CupRankDto> cupRanks = List.of(
                    new CupRankDto(1L, 1),
                    new CupRankDto(2L, 2),
                    new CupRankDto(3L, 3)
            );
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when & then
            assertThatThrownBy(() -> cupService.updateRanks(request, other.getId()))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(FORBIDDEN.name());
        }
    }
}
