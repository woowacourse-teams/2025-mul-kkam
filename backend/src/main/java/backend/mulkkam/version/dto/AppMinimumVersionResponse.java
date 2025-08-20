package backend.mulkkam.version.dto;

import backend.mulkkam.version.domain.AppMinimumVersion;

public record AppMinimumVersionResponse(String minimumVersion) {
    public AppMinimumVersionResponse(AppMinimumVersion appMinimumVersion) {
        this(appMinimumVersion.getMinimumVersion());
    }
}
