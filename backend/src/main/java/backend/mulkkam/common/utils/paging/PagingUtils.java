package backend.mulkkam.common.utils.paging;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;

import backend.mulkkam.common.exception.CommonException;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;

public final class PagingUtils {

    private PagingUtils() {
    }

    public static <T, R, C extends Comparable<C>> PagingResult<R, C> toPagingResult(
            List<T> fetched,
            int size,
            Function<T, R> mapper,
            Function<T, C> cursorFinder
    ) {
        validatePageSize(size);

        boolean hasNext = fetched.size() > size;

        List<T> trimmed = hasNext ? fetched.subList(0, size) : fetched;
        C nextCursor = trimmed.isEmpty() ? null : cursorFinder.apply(trimmed.getLast());
        List<R> content = trimmed.stream().map(mapper).toList();

        return new PagingResult<>(content, nextCursor, hasNext);
    }

    public static PageRequest createPageRequest(int size) {
        return PageRequest.of(0, size + 1);
    }

    private static void validatePageSize(int size) {
        if (size <= 0) {
            throw new CommonException(INVALID_PAGE_SIZE_RANGE);
        }
    }
}
