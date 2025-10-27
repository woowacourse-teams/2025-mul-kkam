package backend.mulkkam.friend.service.command;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.EXCEED_FRIEND_REMINDER_LIMIT;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.repository.FriendReminderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FriendReminderHistoryCommandService {

    private final FriendReminderHistoryRepository friendReminderHistoryRepository;

    public void reduceRemainingCount(Long id) {
        int updated = friendReminderHistoryRepository.tryReduceRemaining(id);
        if (updated == 0) {
            throw new CommonException(EXCEED_FRIEND_REMINDER_LIMIT);
        }
    }
}
