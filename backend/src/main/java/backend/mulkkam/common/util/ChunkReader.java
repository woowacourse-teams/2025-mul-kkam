package backend.mulkkam.common.util;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public final class ChunkReader {

    public static <T> List<T> readChunk(
            ChunkQueryFunction<T> queryFunction,
            Long lastId,
            int chunkSize
    ) {
        return queryFunction.query(
                lastId,
                PageRequest.of(0, chunkSize, Sort.by("id"))
        );
    }
}
