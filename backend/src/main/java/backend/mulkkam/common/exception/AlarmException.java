package backend.mulkkam.common.exception;

import backend.mulkkam.common.exception.errorCode.ErrorCode;
import backend.mulkkam.common.exception.errorCode.FirebaseErrorCode;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;

public class AlarmException extends CommonException {

    public AlarmException(FirebaseMessagingException firebaseMessagingException) {
        super(getErrorCode(firebaseMessagingException));
    }

    private static ErrorCode getErrorCode(FirebaseMessagingException firebaseMessagingException) {
        MessagingErrorCode messagingErrorCode = firebaseMessagingException.getMessagingErrorCode();
        return FirebaseErrorCode.findByName(messagingErrorCode.name());
    }
}
