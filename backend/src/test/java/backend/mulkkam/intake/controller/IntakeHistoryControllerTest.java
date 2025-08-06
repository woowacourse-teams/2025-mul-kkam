package backend.mulkkam.intake.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class IntakeHistoryControllerTest {

    @DisplayName("Filter 검증")
    @Nested
    class AuthFilter {

        @DisplayName("GET /intake/history 요청을 보낼 때")
        @Nested
        class GetHistory {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().get("/intake/history")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().get("/intake/history")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @DisplayName("POST /intake/history 요청을 보낼 때")
        @Nested
        class PostHistory {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() {
                RestAssured.given().log().all()
                        .when().post("/intake/history")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() {
                RestAssured.given().log().all()
                        .header("Authorization", "Basic token")
                        .when().post("/intake/history")
                        .then().log().all()
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }
    }
}
