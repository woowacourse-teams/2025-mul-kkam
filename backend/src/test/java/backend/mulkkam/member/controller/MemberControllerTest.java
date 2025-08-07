package backend.mulkkam.member.controller;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.OnboardingStatusResponse;
import backend.mulkkam.support.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MemberControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @DisplayName("Filter 검증")
    @Nested
    class AuthFilter {

        @DisplayName("GET /members/{id} 요청을 보낼 때")
        @Nested
        class GetMember {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().get("/members/1")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/members/1")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("POST /members/physical-attributes 요청을 보낼 때")
        @Nested
        class PostPhysicalAttributes {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().post("/members/physical-attributes")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().post("/members/physical-attributes")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("GET /members/nickname/validation 요청을 보낼 때")
        @Nested
        class GetNicknameValidation {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().get("/members/nickname/validation")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/members/nickname/validation")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("PATCH /members/nickname 요청을 보낼 때")
        @Nested
        class PatchNickname {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().patch("/members/nickname")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().patch("/members/nickname")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("GET /members/nickname 요청을 보낼 때")
        @Nested
        class GetNickname {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().get("/members/nickname")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/members/nickname")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }
    }

    @DisplayName("온보딩 진행 시")
    @Nested
    class Create {

        @BeforeEach
        void setUp() {
            databaseCleaner.clean();
        }

        @DisplayName("토큰에 대한 OauthAccount 를 추출해 정상적으로 저장이 이뤄진다")
        @Test
        void success_withValidHeader() {
            // given
            OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            String token = oauthJwtTokenHandler.createToken(oauthAccount);

            CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                    "히로",
                    70.0,
                    Gender.FEMALE,
                    1_000,
                    true,
                    false
            );

            // when
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(createMemberRequest)
                    .when().post("/members")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // then
            OauthAccount savedOauthAccount = oauthAccountRepository.findById(oauthAccount.getId()).get();
            assertThat(savedOauthAccount.getMember()).isNotNull();
        }

        @DisplayName("생체 정보 없이도 저장이 정상적으로 이뤄진다")
        @Test
        void success_withoutPhysicalAttributes() {
            // given
            OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            String token = oauthJwtTokenHandler.createToken(oauthAccount);

            CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                    "히로",
                    null,
                    null,
                    1_000,
                    true,
                    false
            );

            // when
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(createMemberRequest)
                    .when().post("/members")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // then
            OauthAccount savedOauthAccount = oauthAccountRepository.findById(oauthAccount.getId()).get();
            assertThat(savedOauthAccount.getMember()).isNotNull();
        }
    }

    @DisplayName("온보딩 진행 여부 확인 시")
    @Nested
    class CheckOnboardingStatus {

        @BeforeEach
        void setUp() {
            databaseCleaner.clean();
        }

        @DisplayName("정상적으로 온보딩 진행 여부가 반환된다")
        @Test
        void success_withValidHeader() {
            // given
            OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            String token = oauthJwtTokenHandler.createToken(oauthAccount);

            // when
            OnboardingStatusResponse response = RestAssured.given().log().all()
                    .header("Authorization", "Bearer " + token)
                    .when().get("/members/check/onboarding")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(OnboardingStatusResponse.class);

            // then
            assertThat(response.finishedOnboarding()).isFalse();
        }
    }
}
