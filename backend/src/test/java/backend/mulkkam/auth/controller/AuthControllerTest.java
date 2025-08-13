package backend.mulkkam.auth.controller;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.dto.request.KakaoSigninRequest;
import backend.mulkkam.auth.dto.request.ReissueTokenRequest;
import backend.mulkkam.auth.dto.response.OauthLoginResponse;
import backend.mulkkam.auth.dto.response.ReissueTokenResponse;
import backend.mulkkam.auth.infrastructure.KakaoRestClient;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.member.dto.response.KakaoUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Key;
import java.util.Date;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_TOKEN;
import static backend.mulkkam.common.exception.errorCode.UnauthorizedErrorCode.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private final String oauthAccessToken = "abcdefg";
    private final KakaoUserInfo userInfo = new KakaoUserInfo("abc");

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @MockitoBean
    private KakaoRestClient kakaoRestClient;

    @BeforeEach
    void setup() {
        when(kakaoRestClient.getUserInfo(oauthAccessToken)).thenReturn(userInfo);
    }

    @DisplayName("액세스 토큰 재발급 요청에서")
    @Nested
    class ReissueToken {

        @DisplayName("로그인을 완료한 이후, 유효한 리프레시 토큰으로 재발급 받을 수 있다.")
        @Test
        void success_afterLogin() throws Exception {
            // given
            KakaoSigninRequest loginRequest = new KakaoSigninRequest(oauthAccessToken);
            String loginRequestContent = objectMapper.writeValueAsString(loginRequest);

            String content = mockMvc.perform(post("/auth/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestContent))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            OauthLoginResponse loginResponse = objectMapper.readValue(content, OauthLoginResponse.class);

            // when
            ReissueTokenRequest request = new ReissueTokenRequest(loginResponse.refreshToken());
            String requestContent = objectMapper.writeValueAsString(request);

            String resultContent = mockMvc.perform(post("/auth/token/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestContent))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            ReissueTokenResponse response = objectMapper.readValue(resultContent, ReissueTokenResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.accessToken()).isNotEqualTo(loginResponse.accessToken());
                softly.assertThat(response.refreshToken()).isNotEqualTo(loginResponse.refreshToken());
            });
        }

        @DisplayName("유효하지 않은 리프레시 토큰으로는 재발급을 받을 수 없다.")
        @Test
        void error_invalidRefreshToken() throws Exception {
            // given
            String invalidToken = "invalid";

            // when
            ReissueTokenRequest request = new ReissueTokenRequest(invalidToken);
            String requestContent = objectMapper.writeValueAsString(request);

            String resultContent = mockMvc.perform(post("/auth/token/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestContent))
                    .andExpect(status().is4xxClientError())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            FailureBody response = objectMapper.readValue(resultContent, FailureBody.class);

            // then
            assertThat(response.getCode()).isEqualTo(INVALID_TOKEN.name());
        }

        @DisplayName("이미 사용된 리프레시 토큰으로는 재발급 받을 수 없다.")
        @Test
        void error_usedRefreshToken() throws Exception {
            // given
            KakaoSigninRequest loginRequest = new KakaoSigninRequest(oauthAccessToken);
            String loginRequestContent = objectMapper.writeValueAsString(loginRequest);

            String content = mockMvc.perform(post("/auth/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestContent))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            OauthLoginResponse loginResponse = objectMapper.readValue(content, OauthLoginResponse.class);

            ReissueTokenRequest request = new ReissueTokenRequest(loginResponse.refreshToken());

            mockMvc.perform(post("/auth/token/reissue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // when
            String resultContent = mockMvc.perform(post("/auth/token/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            FailureBody response = objectMapper.readValue(resultContent, FailureBody.class);

            // then
            assertThat(response.getCode()).isEqualTo(INVALID_TOKEN.name());
        }

        @DisplayName("리프레시 토큰이 만료된 경우 재발급을 받을 수 없다.")
        @Test
        void error_expiredRefreshToken() throws Exception {
            // given
            KakaoSigninRequest loginRequest = new KakaoSigninRequest(oauthAccessToken);

            mockMvc.perform(post("/auth/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk());

            OauthAccount account = oauthAccountRepository.findByOauthId(userInfo.oauthMemberId()).orElseThrow();
            String expiredRefreshToken = generateExpiredRefreshToken(account);

            // when
            ReissueTokenRequest request = new ReissueTokenRequest(expiredRefreshToken);

            String resultContent = mockMvc.perform(post("/auth/token/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            FailureBody response = objectMapper.readValue(resultContent, FailureBody.class);

            // then
            assertThat(response.getCode()).isEqualTo(INVALID_TOKEN.name());
        }

        @DisplayName("존재하지 않는 회원 정보가 담긴 토큰으로는 재발급할 수 없다.")
        @Test
        void error_notExistAccount() throws Exception {
            // given
            OauthAccount notSavedAccount = new OauthAccount(-1L, "notExist", OauthProvider.KAKAO);

            String refreshToken = generateRefreshToken(notSavedAccount);

            // when
            ReissueTokenRequest request = new ReissueTokenRequest(refreshToken);
            String requestContent = objectMapper.writeValueAsString(request);

            String resultContent = mockMvc.perform(post("/auth/token/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestContent))
                    .andExpect(status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            FailureBody response = objectMapper.readValue(resultContent, FailureBody.class);

            // then
            assertThat(response.getCode()).isEqualTo(UNAUTHORIZED.name());
        }
    }

    private String generateExpiredRefreshToken(OauthAccount account) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Date now = new Date();
        Date pastIssuedAt = new Date(now.getTime() - 60_000);
        Date pastExpiration = new Date(now.getTime() - 1_000);

        Claims claims = Jwts.claims()
                .subject(account.getId().toString())
                .build();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(pastIssuedAt)
                .expiration(pastExpiration)
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(OauthAccount account) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Date now = new Date();
        Date issuedAt = new Date(now.getTime());
        Date expiration = new Date(now.getTime() + 300_000); // 5분

        Claims claims = Jwts.claims()
                .subject(account.getId().toString())
                .build();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}
