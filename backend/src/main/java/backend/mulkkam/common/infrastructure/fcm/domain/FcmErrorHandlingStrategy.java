package backend.mulkkam.common.infrastructure.fcm.domain;

import backend.mulkkam.common.exception.errorCode.FirebaseErrorCode;
import java.util.EnumMap;
import java.util.Map;

public enum FcmErrorHandlingStrategy {
    UNREGISTERED(false, true, true),
    INVALID_ARGUMENT(false, true, true),
    INTERNAL(true, false, false),
    UNAVAILABLE(true, false, false),
    QUOTA_EXCEEDED(true, false, false),
    SENDER_ID_MISMATCH(false, false, true),
    THIRD_PARTY_AUTH_ERROR(false, false, true);

    private static final Map<FirebaseErrorCode, FcmErrorHandlingStrategy> STRATEGIES =
            new EnumMap<>(FirebaseErrorCode.class);

    static {
        STRATEGIES.put(FirebaseErrorCode.UNREGISTERED, UNREGISTERED);
        STRATEGIES.put(FirebaseErrorCode.INVALID_ARGUMENT, INVALID_ARGUMENT);
        STRATEGIES.put(FirebaseErrorCode.INTERNAL, INTERNAL);
        STRATEGIES.put(FirebaseErrorCode.UNAVAILABLE, UNAVAILABLE);
        STRATEGIES.put(FirebaseErrorCode.QUOTA_EXCEEDED, QUOTA_EXCEEDED);
        STRATEGIES.put(FirebaseErrorCode.SENDER_ID_MISMATCH, SENDER_ID_MISMATCH);
        STRATEGIES.put(FirebaseErrorCode.THIRD_PARTY_AUTH_ERROR, THIRD_PARTY_AUTH_ERROR);
    }

    private final boolean retryable;
    private final boolean removeToken;
    private final boolean failFast;

    FcmErrorHandlingStrategy(boolean retryable, boolean removeToken, boolean failFast) {
        this.retryable = retryable;
        this.removeToken = removeToken;
        this.failFast = failFast;
    }

    public static FcmErrorHandlingStrategy from(FirebaseErrorCode errorCode) {
        return STRATEGIES.getOrDefault(errorCode, INTERNAL);
    }

    public boolean isRetryable() {
        return retryable;
    }

    public boolean shouldRemoveToken() {
        return removeToken;
    }

    public boolean shouldFailFast() {
        return failFast;
    }
}
