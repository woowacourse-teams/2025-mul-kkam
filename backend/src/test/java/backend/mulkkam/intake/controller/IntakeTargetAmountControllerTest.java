package backend.mulkkam.intake.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class IntakeTargetAmountControllerTest {

    @DisplayName("Filter 검증")
    @Nested
    class AuthFilter {

        @DisplayName("GET /intake/amount/recommended 요청을 보낼 때")
        @Nested
        class GetRecommended {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().get("/intake/amount/recommended")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/intake/amount/recommended")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("GET /intake/amount/target 요청을 보낼 때")
        @Nested
        class GetTarget {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().get("/intake/amount/target")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/intake/amount/target")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("PATCH /intake/amount/target 요청을 보낼 때")
        @Nested
        class PatchTarget {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().patch("/intake/amount/target")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().patch("/intake/amount/target")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }
    }
}
