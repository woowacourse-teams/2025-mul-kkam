package backend.mulkkam.member.service;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_MEMBER_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_TARGET_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.dto.response.ProgressInfoResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.support.AccountRefreshTokenFixtureBuilder;
import backend.mulkkam.support.CupFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.OauthAccountFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private IntakeHistoryDetailRepository intakeDetailRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Autowired
    private CupRepository cupRepository;

    @Autowired
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @DisplayName("멤버를 조회할 때")
    @Nested
    class Get {

        @DisplayName("존재하는 ID로 조회 시 멤버 정보를 반환한다")
        @Test
        void success_whenExistingId() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            memberRepository.save(member);

            // when
            MemberResponse result = memberService.get(member);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.id()).isEqualTo(member.getId());
                softly.assertThat(result.nickname()).isEqualTo(member.getMemberNickname().value());
                softly.assertThat(result.weight()).isEqualTo(member.getPhysicalAttributes().getWeight());
                softly.assertThat(result.gender()).isEqualTo(member.getPhysicalAttributes().getGender().name());
                softly.assertThat(result.targetAmount()).isEqualTo(member.getTargetAmount().value());
            });
        }
    }

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
                    member
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
            MemberNicknameModifyRequest memberNicknameModifyRequest = new MemberNicknameModifyRequest(
                    modifyNickname);

            // when
            memberService.modifyNickname(
                    memberNicknameModifyRequest,
                    member
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
                    member
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
                    member1
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
            memberRepository.save(member);

            String expected = member.getMemberNickname().value();

            // when
            MemberNicknameResponse memberNicknameResponse = memberService.getNickname(member);

            // then
            assertThat(memberNicknameResponse.memberNickname()).isEqualTo(expected);
        }
    }

    @DisplayName("온보딩 시에")
    @Nested
    class Create {

        @DisplayName("정상적으로 회원이 저장된다")
        @Test
        void success_validData() {
            // given
            String rawNickname = "히로";
            double weight = 60.0;
            Gender gender = Gender.FEMALE;
            int rawTargetIntakeAmount = 1_000;
            CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                    rawNickname,
                    weight,
                    gender,
                    rawTargetIntakeAmount,
                    true,
                    false
            );

            OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            // when
            memberService.create(oauthAccount, createMemberRequest);

            // then
            List<Member> savedMembers = memberRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(savedMembers.size()).isEqualTo(1);
                softly.assertThat(savedMembers.getFirst().getMemberNickname())
                        .isEqualTo(new MemberNickname(rawNickname));
                softly.assertThat(savedMembers.getFirst().getPhysicalAttributes().getWeight()).isEqualTo(weight);
                softly.assertThat(savedMembers.getFirst().getPhysicalAttributes().getGender()).isEqualTo(gender);
                softly.assertThat(savedMembers.getFirst().getTargetAmount())
                        .isEqualTo(new TargetAmount(rawTargetIntakeAmount));
            });
        }

        @DisplayName("유효하지 않은 닉네임을 사용할 경우 예외를 반환한다")
        @ParameterizedTest
        @ValueSource(strings = {"1", " ", "", "1234567891011"})
        void error_invalidNickname(String invalidNickname) {
            // given
            double weight = 60.0;
            Gender gender = Gender.FEMALE;
            int rawTargetIntakeAmount = 1_000;
            CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                    invalidNickname,
                    weight,
                    gender,
                    rawTargetIntakeAmount,
                    true,
                    false
            );

            OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            // when & then
            assertThatThrownBy(() -> memberService.create(oauthAccount, createMemberRequest))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_MEMBER_NICKNAME.name());
        }
    }

    @DisplayName("유효하지 않은 목표 음용량을 사용할 경우 예외를 반환한다")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -3})
    void error_invalidTargetAmount(int invalidIntakeAmount) {
        // given
        String rawNickname = "히로";
        double weight = 60.0;
        Gender gender = Gender.FEMALE;
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                rawNickname,
                weight,
                gender,
                invalidIntakeAmount,
                true,
                false
        );

        OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);

        // when & then
        assertThatThrownBy(() -> memberService.create(oauthAccount, createMemberRequest))
                .isInstanceOf(CommonException.class)
                .hasMessage(INVALID_TARGET_AMOUNT.name());
    }

    @DisplayName("멤버의 진행 상황을 조회할 때")
    @Nested
    class GetProgressInfo {

        @DisplayName("정상적으로 작동한다")
        @Test
        void success_validData() {
            // given
            String nickname = "체체";
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname(nickname))
                    .build();
            memberRepository.save(member);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetIntakeAmount(new TargetAmount(1000))
                    .date(LocalDate.of(2025, 7, 15))
                    .streak(42)
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .build();
            intakeDetailRepository.save(intakeHistoryDetail);

            // when
            ProgressInfoResponse progressInfoResponse = memberService.getProgressInfo(
                    member,
                    LocalDate.of(2025, 7, 15)
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(progressInfoResponse.memberNickname()).isEqualTo(nickname);
                softly.assertThat(progressInfoResponse.streak()).isEqualTo(42);
                softly.assertThat(progressInfoResponse.achievementRate()).isEqualTo(50.0);
                softly.assertThat(progressInfoResponse.targetAmount()).isEqualTo(1000);
                softly.assertThat(progressInfoResponse.totalAmount()).isEqualTo(500);
            });
        }

        @DisplayName("오늘의 기록이 존재하지 않는 경우 멤버의 목표 음용량을 조회한다")
        @Test
        void success_withoutIntakeHistory() {
            // given
            String nickname = "체체";
            int rawTargetAmount = 1_000;

            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname(nickname))
                    .targetAmount(new Amount(rawTargetAmount))
                    .build();
            memberRepository.save(member);

            LocalDate date = LocalDate.of(2025, 3, 25);

            // when
            ProgressInfoResponse progressInfoResponse = memberService.getProgressInfo(
                    member,
                    date.plusDays(1)
            );

            // then
            assertSoftly(softly -> {
                softly.assertThat(progressInfoResponse.memberNickname()).isEqualTo(nickname);
                softly.assertThat(progressInfoResponse.streak()).isEqualTo(1);
                softly.assertThat(progressInfoResponse.achievementRate()).isEqualTo(0);
                softly.assertThat(progressInfoResponse.targetAmount()).isEqualTo(rawTargetAmount);
                softly.assertThat(progressInfoResponse.totalAmount()).isEqualTo(0);
            });
        }
    }

    @DisplayName("회원을 삭제할 때")
    @Nested
    class Delete {

        @DisplayName("정상적으로 멤버가 삭제된다")
        @Test
        void success_deleteMember() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            memberRepository.save(member);

            OauthAccount oauthAccount = OauthAccountFixtureBuilder
                    .withMember(member)
                    .build();
            oauthAccountRepository.save(oauthAccount);

            AccountRefreshToken accountRefreshToken = AccountRefreshTokenFixtureBuilder.withOauthAccount(oauthAccount)
                    .build();
            accountRefreshTokenRepository.save(accountRefreshToken);

            // when
            memberService.delete(member);

            // then
            assertThat(memberRepository.findById(member.getId())).isEmpty();
        }

        @DisplayName("정상적으로 토큰이 삭제된다")
        @Test
        void success_deleteRefreshToken() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            memberRepository.save(member);

            OauthAccount oauthAccount = OauthAccountFixtureBuilder
                    .withMember(member)
                    .build();
            oauthAccountRepository.save(oauthAccount);

            AccountRefreshToken accountRefreshToken = AccountRefreshTokenFixtureBuilder.withOauthAccount(oauthAccount)
                    .build();
            accountRefreshTokenRepository.save(accountRefreshToken);

            // when
            memberService.delete(member);

            // then
            assertSoftly(softly -> {
                softly.assertThat(accountRefreshTokenRepository.findById(accountRefreshToken.getId())).isEmpty();
                softly.assertThat(oauthAccountRepository.findById(oauthAccount.getId())).isEmpty();
            });
        }

        @DisplayName("연관된 모든 엔티티가 제거된다")
        @Test
        void success_deleteAllRelatedEntities() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            memberRepository.save(member);

            OauthAccount oauthAccount = OauthAccountFixtureBuilder
                    .withMember(member)
                    .build();
            oauthAccountRepository.save(oauthAccount);

            AccountRefreshToken accountRefreshToken = AccountRefreshTokenFixtureBuilder.withOauthAccount(oauthAccount)
                    .build();
            accountRefreshTokenRepository.save(accountRefreshToken);

            Cup cup = CupFixtureBuilder.withMember(member)
                    .build();
            cupRepository.save(cup);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder.withMember(member)
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .build();
            intakeHistoryDetailRepository.save(intakeHistoryDetail);

            Device device = new Device("token", "id", member);
            deviceRepository.save(device);

            Notification notification = new Notification(NotificationType.NOTICE, "title", LocalDateTime.now(),
                    new Amount(1_000), member);
            notificationRepository.save(notification);

            // when
            memberService.delete(member);

            // then
            assertSoftly(softly -> {
                softly.assertThat(accountRefreshTokenRepository.findById(accountRefreshToken.getId())).isEmpty();
                softly.assertThat(oauthAccountRepository.findById(oauthAccount.getId())).isEmpty();
                softly.assertThat(cupRepository.findById(cup.getId())).isEmpty();
                softly.assertThat(intakeHistoryRepository.findById(intakeHistory.getId())).isEmpty();
                softly.assertThat(intakeHistoryDetailRepository.findById(intakeHistoryDetail.getId())).isEmpty();
                softly.assertThat(deviceRepository.findById(device.getId())).isEmpty();
                softly.assertThat(notificationRepository.findById(notification.getId())).isEmpty();
            });
        }
    }
}
