package backend.mulkkam.device.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.DeviceFixtureBuilder;
import backend.mulkkam.support.fixture.MemberFixtureBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class DeviceControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    private Member savedMember;
    private String token;

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder
                .builder().build();
        savedMember = memberRepository.save(member);

        OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);

        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
    }

    @DisplayName("기기의 FCM 토큰을 삭제할 때")
    @Nested
    class DeleteFcmToken {

        @DisplayName("Device Id 가 존재하는 경우, 정상적으로 삭제된다")
        @Test
        void success_whenDeviceIdIsExited() throws Exception {
            // given
            String deviceId = "deviceId";

            Device device = DeviceFixtureBuilder
                    .withMember(savedMember)
                    .deviceId(deviceId)
                    .build();
            deviceRepository.save(device);

            // when
            mockMvc.perform(delete("/devices/fcm-token")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .header("X-Device-Id", deviceId))
                    .andDo(print())
                    .andExpect(status().isOk());

            // then
            Device updatedDevice = deviceRepository.findById(device.getId()).get();
            assertThat(updatedDevice.getToken()).isNull();
        }
    }
}
