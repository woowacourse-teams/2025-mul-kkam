package backend.mulkkam.version.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_APP_MINIMUM_VERSION;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.version.domain.AppMinimumVersion;
import backend.mulkkam.version.dto.AppMinimumVersionResponse;
import backend.mulkkam.version.repository.AppMinimumVersionRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AppMinimumVersionService {

    private final AppMinimumVersionRepository appMinimumVersionRepository;

    public AppMinimumVersionResponse read() {
        Optional<AppMinimumVersion> latestAppMinimumVersion = appMinimumVersionRepository.findFirstByOrderByUpdatedAtDesc();
        return latestAppMinimumVersion
                .map(AppMinimumVersionResponse::new)
                .orElseThrow(() -> new CommonException(NOT_FOUND_APP_MINIMUM_VERSION));
    }
}
