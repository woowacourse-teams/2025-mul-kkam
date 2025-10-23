package backend.mulkkam.common.utils.paging;

import java.util.List;

public record PagingResult<R, C>(
        List<R> content,
        C nextCursor,
        boolean hasNext
) {
}

