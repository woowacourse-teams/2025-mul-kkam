package backend.mulkkam.common.filter;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class JwtAuthenticationFilterTest {

    @DisplayName("Filter ignore 검증")
    @Nested
    class IgnoreFilter {

        @DisplayName("POST /auth/* 요청을 보낼 때")
        @Nested
        class PostAuth {

            @DisplayName("카카오 로그인시 인증 헤더가 존재하지 않더라도 인증 필터를 통과할 수 있다.")
            @Test
            void success_withoutAuthorizationHeader() {
                // given & when
                int statusCode = RestAssured.given().log().all()
                        .when().post("/auth/kakao")
                        .then().log().all()
                        .extract()
                        .statusCode();

                // then
                assertThat(statusCode).isNotEqualTo(HttpStatus.UNAUTHORIZED.value());
            }
        }
    }
}
