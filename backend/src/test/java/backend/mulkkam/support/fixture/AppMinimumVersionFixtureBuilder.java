package backend.mulkkam.support.fixture;

import backend.mulkkam.version.domain.AppMinimumVersion;
import java.time.LocalDateTime;

public class AppMinimumVersionFixtureBuilder {

    private String minimumVersion = "1.0.0";
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static AppMinimumVersionFixtureBuilder builder() {
        return new AppMinimumVersionFixtureBuilder();
    }

    public AppMinimumVersionFixtureBuilder minimumVersion(String minimumVersion) {
        this.minimumVersion = minimumVersion;
        return this;
    }

    public AppMinimumVersionFixtureBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public AppMinimumVersion build() {
        return new AppMinimumVersion(
                minimumVersion,
                updatedAt
        );
    }
}
