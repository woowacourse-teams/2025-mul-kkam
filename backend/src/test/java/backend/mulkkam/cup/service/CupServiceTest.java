package backend.mulkkam.cup.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP_RANKS;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.CupRankDto;
import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.dto.request.CreateCupWithoutRankRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupResponse;
import backend.mulkkam.cup.dto.response.CupsResponse;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.cup.CupFixtureBuilder;
import backend.mulkkam.support.fixture.cup.dto.CreateCupRequestFixtureBuilder;
import backend.mulkkam.support.fixture.cup.dto.CreateCupWithoutRankRequestFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceTest;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CupServiceTest extends ServiceTest {

    @Autowired
    private CupService cupService;

    @Autowired
    private CupRepository cupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CupEmojiRepository cupEmojiRepository;

    private Member createAndSaveMember() {
        return memberRepository.save(MemberFixtureBuilder.builder().build());
    }

    private Member createAndSaveMember(String nickname) {
        return memberRepository.save(
                MemberFixtureBuilder.builder().memberNickname(new MemberNickname(nickname)).build()
        );
    }

    private CupEmoji createAndSaveCupEmoji() {
        return cupEmojiRepository.save(new CupEmoji(
                "https://github.com/user-attachments/assets/783767ab-ee37-4079-8e38-e08884a8de1c"
        ));
    }

    @DisplayName("컵 리스트를 생성할 때에")
    @Nested
    class CreateAll {

        @DisplayName("정상적으로 저장한다")
        @Test
        void success_validData() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            List<CreateCupRequest> createCupRequests = List.of(
                    CreateCupRequestFixtureBuilder
                            .withCupEmojiId(savedCupEmoji.getId())
                            .cupRank(1)
                            .build(),
                    CreateCupRequestFixtureBuilder
                            .withCupEmojiId(savedCupEmoji.getId())
                            .cupRank(2)
                            .build(),
                    CreateCupRequestFixtureBuilder
                            .withCupEmojiId(savedCupEmoji.getId())
                            .cupRank(3)
                            .build()
            );

            // when
            cupService.createAll(createCupRequests, savedMember);

            // then
            List<Cup> cups = cupRepository.findAllByMember(savedMember);
            List<Integer> cupRanks = cups.stream()
                    .map(cup -> cup.getCupRank().value())
                    .toList();

            assertSoftly(softly -> {
                        softly.assertThat(cups.size()).isEqualTo(createCupRequests.size());
                        softly.assertThat(cupRanks).containsAll(List.of(1, 2, 3));
                    }
            );
        }
    }

    @DisplayName("랭크 없이 컵을 생성할 때에")
    @Nested
    class CreateAtLastRank {

        @DisplayName("랭크는 마지막 랭크로 정상적으로 저장한다")
        @Test
        void success_validData() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();
            Long cupEmojiId = savedCupEmoji.getId();

            CreateCupWithoutRankRequest cupRegisterRequest = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(cupEmojiId)
                    .build();

            // when
            CupResponse result = cupService.createAtLastRank(cupRegisterRequest, new MemberDetails(savedMember));

            // then
            CupRank actualMaxCupRank = cupRepository.findAllByMember(savedMember).stream()
                    .map(Cup::getCupRank)
                    .max(Comparator.comparingInt(CupRank::value))
                    .orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(result.cupNickname()).isEqualTo(cupRegisterRequest.cupNickname());
                softly.assertThat(result.cupAmount()).isEqualTo(cupRegisterRequest.cupAmount());
                softly.assertThat(result.intakeType()).isEqualTo(IntakeType.WATER);
                softly.assertThat(cupRepository.findById(result.id())).isPresent();
                softly.assertThat(result.cupRank())
                        .isEqualTo(actualMaxCupRank.value());
            });
        }

        @DisplayName("세 개의 컵이 삭제되었다가 추가되는 경우, 우선순위가 중복되지 않는다.")
        @Test
        void success_createAfterDeleted() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();
            Long cupEmojiId = savedCupEmoji.getId();

            Cup firstCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(1))
                    .build();

            Cup secondCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(2))
                    .build();

            Cup thirdCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(3))
                    .build();

            cupRepository.save(firstCup);
            cupRepository.save(secondCup);
            cupRepository.save(thirdCup);

            cupService.delete(thirdCup.getId(), new MemberDetails(savedMember));

            CreateCupWithoutRankRequest request = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(cupEmojiId)
                    .build();

            // when
            cupService.createAtLastRank(request, new MemberDetails(savedMember));

            // then
            assertSoftly(softly -> {
                List<Cup> cups = cupRepository.findAllByMember(savedMember);
                softly.assertThat(cups).hasSize(3);
                softly.assertThat(cups.stream().map(Cup::getCupRank)).doesNotHaveDuplicates();
            });
        }

        @DisplayName("용량이 음수면 예외가 발생한다")
        @Test
        void error_amountLessThan0() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            CreateCupWithoutRankRequest registerCupRequest = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(savedCupEmoji.getId())
                    .cupAmount(-100)
                    .build();

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.createAtLastRank(registerCupRequest, new MemberDetails(savedMember)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
        }

        @DisplayName("용량이 0이면 예외가 발생한다")
        @Test
        void error_amountIsEqualTo0() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            CreateCupWithoutRankRequest registerCupRequest = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(savedCupEmoji.getId())
                    .cupAmount(0)
                    .build();

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.createAtLastRank(registerCupRequest, new MemberDetails(savedMember)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
        }

        @DisplayName("컵이 3개 저장되어 있을 때 예외가 발생한다")
        @Test
        void error_memberAlreadyHasThreeCups() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();
            Long cupEmojiId = savedCupEmoji.getId();

            CreateCupWithoutRankRequest registerCupRequest = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(cupEmojiId)
                    .build();
            CreateCupWithoutRankRequest registerCupRequest1 = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(cupEmojiId)
                    .build();
            CreateCupWithoutRankRequest registerCupRequest2 = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(cupEmojiId)
                    .build();
            CreateCupWithoutRankRequest registerCupRequest3 = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(cupEmojiId)
                    .build();

            // when
            cupService.createAtLastRank(registerCupRequest1, new MemberDetails(savedMember));
            cupService.createAtLastRank(registerCupRequest2, new MemberDetails(savedMember));
            cupService.createAtLastRank(registerCupRequest3, new MemberDetails(savedMember));

            // then
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.createAtLastRank(registerCupRequest, new MemberDetails(savedMember)));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_COUNT);
        }
    }

    @DisplayName("컵을 읽을 때에")
    @Nested
    class ReadSortedCupsByMemberId {

        @DisplayName("사용자의 컵을 랭크순으로 모두 가져온다")
        @Test
        void success_withExistedMemberId() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            Cup cup1 = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(2))
                    .build();

            Cup cup2 = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(1))
                    .build();
            List<Cup> cups = List.of(cup1, cup2);
            cupRepository.saveAll(cups);

            // when
            CupsResponse result = cupService.readSortedCupsByMember(new MemberDetails(savedMember));

            CupResponse firstCup = result.cups().getFirst();
            CupResponse secondCup = result.cups().get(1);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.cups().size()).isEqualTo(2);
                softly.assertThat(firstCup.cupNickname()).isEqualTo(cup2.getNickname().value());
                softly.assertThat(firstCup.cupAmount()).isEqualTo(cup2.getCupAmount().value());
                softly.assertThat(firstCup.cupRank()).isEqualTo(cup2.getCupRank().value());
                softly.assertThat(secondCup.cupRank()).isEqualTo(cup1.getCupRank().value());
                List<Integer> ranks = result.cups().stream().map(CupResponse::cupRank).toList();
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
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            String beforeCupNickName = "변경 전";
            Integer beforeCupAmount = 500;
            IntakeType beforeIntakeType = IntakeType.WATER;

            Cup cup = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupAmount(new CupAmount(beforeCupAmount))
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .intakeType(beforeIntakeType)
                    .build();

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;
            IntakeType afterIntakeType = IntakeType.COFFEE;

            Cup savedCup = cupRepository.save(cup);
            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    afterIntakeType,
                    savedCupEmoji.getId()
            );

            // when
            cupService.update(savedCup.getId(), new MemberDetails(savedMember), updateCupRequest);

            Cup changedCup = cupRepository.findById(savedCup.getId()).orElseThrow();

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
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            String beforeCupNickName1 = "변경 전1";
            Integer beforeCupAmount1 = 300;
            IntakeType beforeIntakeType1 = IntakeType.WATER;

            Cup cup1 = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupNickname(new CupNickname(beforeCupNickName1))
                    .cupAmount(new CupAmount(beforeCupAmount1)).
                    intakeType(beforeIntakeType1)
                    .build();

            String beforeCupNickName2 = "변경 전2";
            Integer beforeCupAmount2 = 500;
            IntakeType beforeIntakeType2 = IntakeType.WATER;

            Cup cup2 = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupNickname(new CupNickname(beforeCupNickName2))
                    .cupAmount(new CupAmount(beforeCupAmount2))
                    .intakeType(beforeIntakeType2)
                    .build();

            cupRepository.saveAll(List.of(cup1, cup2));

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;
            IntakeType afterIntakeType = IntakeType.COFFEE;
            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    afterIntakeType,
                    savedCupEmoji.getId()
            );

            // when
            cupService.update(cup1.getId(), new MemberDetails(savedMember), updateCupRequest);

            Cup changedCup1 = cupRepository.findById(cup1.getId()).orElseThrow();
            Cup changedCup2 = cupRepository.findById(cup2.getId()).orElseThrow();

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
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            Member member1 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("멤버1")).build();
            Member member2 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("멤버2")).build();

            memberRepository.saveAll(List.of(member1, member2));

            String beforeCupNickName = "변경 전";
            Integer beforeCupAmount = 500;

            Cup cup = CupFixtureBuilder
                    .withMemberAndCupEmoji(member1, savedCupEmoji)
                    .cupNickname(new CupNickname(beforeCupNickName))
                    .cupAmount(new CupAmount(beforeCupAmount)).build();

            cupRepository.save(cup);

            String afterCupNickName = "변경 후";
            Integer afterCupAmount = 1000;

            UpdateCupRequest updateCupRequest = new UpdateCupRequest(
                    afterCupNickName,
                    afterCupAmount,
                    IntakeType.WATER,
                    savedCupEmoji.getId()
            );

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.update(cup.getId(), new MemberDetails(member2), updateCupRequest));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_PERMITTED_FOR_CUP);
        }
    }

    @DisplayName("컵을 삭제할 때")
    @Nested
    class Delete {

        @DisplayName("우선순위가 더 낮은 컵들의 우선순위가 한 단계씩 승격된다.")
        @Test
        void success_withLowerPriorityCups() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            Cup savedFirstCup = cupRepository.save(CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(1))
                    .build());
            Cup savedSecondCup = cupRepository.save(CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(2))
                    .build());
            Cup savedThirdCup = cupRepository.save(CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(3))
                    .build());

            // when
            cupService.delete(savedFirstCup.getId(), new MemberDetails(savedMember));

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupRepository.existsById(savedFirstCup.getId())).isFalse();
                softly.assertThat(cupRepository.findAllByMember(savedMember)).hasSize(2);
                softly.assertThat(cupRepository.findById(savedSecondCup.getId())).isPresent().get()
                        .extracting(Cup::getCupRank).isEqualTo(new CupRank(1));
                softly.assertThat(cupRepository.findById(savedThirdCup.getId())).isPresent().get()
                        .extracting(Cup::getCupRank).isEqualTo(new CupRank(2));
            });
        }

        @DisplayName("우선순위가 더 낮은 컵이 없는 경우, 그 어떤 컵의 우선순위도 승격되지 않는다.")
        @Test
        void success_withoutLowerPriorityCups() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            Cup savedFirstCup = cupRepository.save(CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(1))
                    .build());
            Cup savedSecondCup = cupRepository.save(CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(2))
                    .build());
            Cup savedThirdCup = cupRepository.save(CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupRank(new CupRank(3))
                    .build());

            // when
            cupService.delete(savedThirdCup.getId(), new MemberDetails(savedMember));

            // then
            assertSoftly(softly -> {
                softly.assertThat(cupRepository.existsById(savedThirdCup.getId())).isFalse();
                softly.assertThat(cupRepository.findAllByMember(savedMember)).hasSize(2);
                softly.assertThat(cupRepository.findById(savedFirstCup.getId())).isPresent().get()
                        .extracting(Cup::getCupRank).isEqualTo(new CupRank(1));
                softly.assertThat(cupRepository.findById(savedSecondCup.getId())).isPresent().get()
                        .extracting(Cup::getCupRank).isEqualTo(new CupRank(2));
            });
        }
    }

    @DisplayName("컵의 우선순위를 변경할 때")
    @Nested
    class UpdateRanks {

        @DisplayName("중복되지 않는 식별자 및 우선순위로 자신의 컵을 수정할 수 있다.")
        @Test
        void success_ifModifyMyCups() {
            // given
            Member savedMember = createAndSaveMember();
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            Cup firstCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupNickname(new CupNickname("first"))
                    .cupRank(new CupRank(1))
                    .build();
            Cup secondCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupNickname(new CupNickname("second"))
                    .cupRank(new CupRank(2))
                    .build();
            Cup thirdCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(savedMember, savedCupEmoji)
                    .cupNickname(new CupNickname("third"))
                    .cupRank(new CupRank(3)).build();

            cupRepository.saveAll(List.of(firstCup, secondCup, thirdCup));

            List<CupRankDto> cupRanks = List.of(new CupRankDto(1L, 3), new CupRankDto(2L, 2), new CupRankDto(3L, 1));
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when & then
            assertSoftly(softly -> {
                softly.assertThatCode(() -> cupService.updateRanks(request, new MemberDetails(savedMember)))
                        .doesNotThrowAnyException();
                softly.assertThat(cupRepository.findById(firstCup.getId()).get().getCupRank())
                        .isEqualTo(new CupRank(3));
                softly.assertThat(cupRepository.findById(secondCup.getId()).get().getCupRank())
                        .isEqualTo(new CupRank(2));
                softly.assertThat(cupRepository.findById(thirdCup.getId()).get().getCupRank())
                        .isEqualTo(new CupRank(1));
            });
        }

        @DisplayName("요청에 존재하지 않는 컵 식별자가 포함된 경우 예외가 발생한다.")
        @Test
        void error_containsNotExistCupId() {
            // given
            Member savedMember = createAndSaveMember();

            List<CupRankDto> cupRanks = List.of(new CupRankDto(1L, 1));
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.updateRanks(request, new MemberDetails(savedMember)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_CUP);
        }

        @DisplayName("중복되는 컵 id가 존재하는 경우 예외가 발생한다.")
        @Test
        void error_existsDuplicatedCupIds() {
            // given
            Member savedMember = createAndSaveMember();

            List<CupRankDto> cupRanks = List.of(new CupRankDto(1L, 1), new CupRankDto(1L, 2), new CupRankDto(2L, 3));
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.updateRanks(request, new MemberDetails(savedMember)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(DUPLICATED_CUP);
        }

        @DisplayName("중복되는 컵 우선순위가 존재하는 경우 예외가 발생한다.")
        @Test
        void error_existsDuplicatedCupRanks() {
            // given
            Member savedMember = createAndSaveMember();

            List<CupRankDto> cupRanks = List.of(new CupRankDto(1L, 1), new CupRankDto(2L, 1), new CupRankDto(3L, 3));
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.updateRanks(request, new MemberDetails(savedMember)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(DUPLICATED_CUP_RANKS);
        }

        @DisplayName("다른 멤버의 컵을 수정하려는 경우 예외가 발생한다.")
        @Test
        void error_ifModifyOtherMemberCup() {
            // given
            CupEmoji savedCupEmoji = createAndSaveCupEmoji();

            Member me = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("me")).build();
            Member other = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("other")).build();

            memberRepository.saveAll(List.of(me, other));

            Cup firstCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(me, savedCupEmoji)
                    .cupNickname(new CupNickname("first"))
                    .build();
            Cup secondCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(other, savedCupEmoji)
                    .cupNickname(new CupNickname("second"))
                    .build();
            Cup thirdCup = CupFixtureBuilder
                    .withMemberAndCupEmoji(other, savedCupEmoji)
                    .cupNickname(new CupNickname("third"))
                    .build();

            cupRepository.saveAll(List.of(firstCup, secondCup, thirdCup));

            List<CupRankDto> cupRanks = List.of(new CupRankDto(1L, 1), new CupRankDto(2L, 2), new CupRankDto(3L, 3));
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(cupRanks);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> cupService.updateRanks(request, new MemberDetails(other)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_PERMITTED_FOR_CUP);
        }
    }
}
