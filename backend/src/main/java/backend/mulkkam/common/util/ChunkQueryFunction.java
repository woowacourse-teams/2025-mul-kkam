package backend.mulkkam.common.util;

import java.util.List;
import org.springframework.data.domain.Pageable;

@FunctionalInterface
public interface ChunkQueryFunction<T> {
    List<T> query(Long lastId, Pageable pageable);
}
