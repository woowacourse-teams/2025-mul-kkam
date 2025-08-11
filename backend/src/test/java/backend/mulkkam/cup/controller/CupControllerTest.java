package backend.mulkkam.cup.controller;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_TYPE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    private String token;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder
                .builder().build();
        memberRepository.save(member);

        OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);

        token = oauthJwtTokenHandler.createToken(oauthAccount);
    }

    // TODO 2025. 8. 11. 11:06: read 메서드도 태스트 할 것

    @DisplayName("컵을 생성한다")
    @Nested
    class Create {

        private final String uri = "/cups";

        @DisplayName("요청하는 컵 데이터가 올바르게 들어왔을 때 컵을 생성한다")
        @Test
        void test1() throws Exception { // TODO 2025. 8. 11. 11:07: 네이밍
            mockMvc.perform(post("/cups")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // TODO 2025. 8. 11. 11:18: 뺄 건지
                            .content(""" 
                                { 
                                  "cupNickname": "머그컵",
                                  "cupAmount": 350,
                                  "intakeType": "WATER",
                                  "emoji": "☕"
                                }
                                """))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("음용 타입이 잘못 들어왔을 때 예외를 던진다")
        @Test
        void test2() throws Exception {
            mockMvc.perform(post("/cups")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(""" 
                                    { 
                                      "cupNickname": "머그컵",
                                      "cupAmount": 350,
                                      "intakeType": "CAR",
                                      "emoji": "☕"
                                    }
                                    """))
                    .andDo(print())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(CommonException.class)
                                    .hasMessage(NOT_FOUND_INTAKE_TYPE.name())
                    );
        }
    }


    @DisplayName("Filter 검증")
    @Nested
    class AuthFilter {

        @DisplayName("GET 요청을 보낼 때")
        @Nested
        class Get {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().get("/cups")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/cups")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("POST 요청을 보낼 때")
        @Nested
        class Post {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().post("/cups")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/cups")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("PUT 요청을 보낼 때")
        @Nested
        class Put {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().put("/cups/ranks")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/cups")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("PATCH 요청을 보낼 때")
        @Nested
        class Patch {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().patch("/cups/1")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/cups")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("DELETE 요청을 보낼 때")
        @Nested
        class Delete {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().delete("/cups/1")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/cups")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }
    }
}
