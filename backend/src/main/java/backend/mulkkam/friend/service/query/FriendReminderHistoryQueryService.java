package backend.mulkkam.friend.service.query;

import static backend.mulkkam.friend.domain.FriendReminderHistory.INIT_REMAINING_VALUE;

import backend.mulkkam.friend.domain.FriendReminderHistory;
import backend.mulkkam.friend.repository.FriendReminderHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@AllArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendReminderHistoryQueryService {

    private final FriendReminderHistoryRepository friendReminderHistoryRepository;

    @Transactional
    public FriendReminderHistory getOrCreateDefault(Long senderId, Long friendId, LocalDate date) {
        return friendReminderHistoryRepository.findBySenderIdAndRecipientIdAndQuotaDate(senderId, friendId, date)
                .orElseGet(() -> {
                    friendReminderHistoryRepository.createIfAbsent(senderId, friendId, date, INIT_REMAINING_VALUE);
                    return get(senderId, friendId, date);
                });
    }

    public FriendReminderHistory get(Long senderId, Long friendId, LocalDate date) {
        return friendReminderHistoryRepository.findBySenderIdAndRecipientIdAndQuotaDate(senderId, friendId, date)
                .orElseThrow();
    }
}
