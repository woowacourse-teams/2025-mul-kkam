package backend.mulkkam.cup.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CupControllerTest {

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
