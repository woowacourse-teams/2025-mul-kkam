package backend.mulkkam.version.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.support.AppMinimumVersionFixtureBuilder;
import backend.mulkkam.version.domain.AppMinimumVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AppMinimumVersionRepositoryTest {

    @Autowired
    AppMinimumVersionRepository appMinimumVersionRepository;

    @DisplayName("가장 최근에 변경된 버전 정보를 가져올 때")
    @Nested
    class FindLatestAppMinimumVersion {

        @DisplayName("가장 최근에 변경된 버전 정보를 가져온다")
        @Test
        void success_withAlreadySavedMultipleVersions() {
            // given
            AppMinimumVersion firstSaved = AppMinimumVersionFixtureBuilder.builder()
                    .updatedAt(LocalDateTime.of(
                            LocalDate.of(2025, 12, 30),
                            LocalTime.of(15, 30)
                    ))
                    .build();
            appMinimumVersionRepository.save(firstSaved);

            AppMinimumVersion secondSaved = AppMinimumVersionFixtureBuilder.builder()
                    .updatedAt(LocalDateTime.of(
                            LocalDate.of(2025, 12, 31),
                            LocalTime.of(15, 30)
                    ))
                    .build();
            appMinimumVersionRepository.save(secondSaved);

            // when
            Optional<AppMinimumVersion> latestAppMinimumVersion = appMinimumVersionRepository.findFirstByOrderByUpdatedAtDesc();

            // then
            assertSoftly(softly -> {
                softly.assertThat(latestAppMinimumVersion).isPresent();
                softly.assertThat(latestAppMinimumVersion.get().getId()).isEqualTo(secondSaved.getId());
            });
        }
    }
}
