package backend.mulkkam.friend.service.command;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.EXCEED_FRIEND_REMINDER_LIMIT;
import static backend.mulkkam.friend.domain.FriendReminderHistory.INIT_REMAINING_VALUE;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.repository.FriendReminderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Transactional
@Service
public class FriendReminderHistoryCommandService {

    private final FriendReminderHistoryRepository friendReminderHistoryRepository;

    public void reduceRemainingCount(Long senderId, Long recipientId, LocalDate date) {
        int updated = friendReminderHistoryRepository.tryReduceRemaining(senderId, recipientId, date);
        if (updated == 0) {
            throw new CommonException(EXCEED_FRIEND_REMINDER_LIMIT);
        }
    }

    public void createIfAbsent(Long senderId, Long friendId, LocalDate today) {
        friendReminderHistoryRepository.createIfAbsent(senderId, friendId, today, INIT_REMAINING_VALUE);
    }
}
