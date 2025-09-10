package backend.mulkkam.version.controller;

import backend.mulkkam.support.fixture.AppMinimumVersionFixtureBuilder;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.version.domain.AppMinimumVersion;
import backend.mulkkam.version.dto.AppMinimumVersionResponse;
import backend.mulkkam.version.repository.AppMinimumVersionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppMinimumVersionControllerTest extends ControllerTest {

    @Autowired
    private AppMinimumVersionRepository appMinimumVersionRepository;

    @DisplayName("앱의 최소 버전을 확인할 때")
    @Nested
    class Get {

        @DisplayName("버전 정보가 존재하는 경우 정상적으로 반환된다")
        @Test
        void success_alreadySavedVersion() throws Exception {
            // given
            String minimumVersion = "1.0.0";
            AppMinimumVersion appMinimumVersion = AppMinimumVersionFixtureBuilder
                    .builder()
                    .minimumVersion(minimumVersion)
                    .build();
            appMinimumVersionRepository.save(appMinimumVersion);

            // when
            String response = mockMvc.perform(get("/versions"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            AppMinimumVersionResponse actual = objectMapper.readValue(response,
                    AppMinimumVersionResponse.class);

            // then
            assertThat(actual.minimumVersion()).isEqualTo(minimumVersion);
        }
    }
}
