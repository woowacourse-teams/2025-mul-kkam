package backend.mulkkam.notification.service;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationBatchService {

    private final NotificationRepository notificationRepository;
    private final EntityManager em;

    //TODO: 추후 TPS 고려 시 알림 엔티티 ID 전략 변경 고려
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOneChunk(
            NotificationMessageTemplate template,
            LocalDateTime now,
            List<Long> memberIds
    ) {
        if (memberIds.isEmpty()) {
            return;
        }
        List<Notification> notifications = memberIds.stream()
                .map(id -> template.toNotification(em.getReference(Member.class, id), now))
                .toList();
        notificationRepository.saveAll(notifications);
        em.flush();
        em.clear();
    }

    public <T> List<T> batchRead( // TODO. 클래스 위치 점검
            BiFunction<Long, Pageable, List<T>> queryFunction,
            Function<T, Long> idExtractor,
            int chunkSize
    ) { // TODO: 함수형 인터페이스
        List<T> allResults = new ArrayList<>();
        Long lastId = null;

        while (true) {
            List<T> batch = queryFunction.apply(
                    lastId,
                    PageRequest.of(0, chunkSize, Sort.by("id"))
            );
            if (batch.isEmpty()) {
                break;
            }

            allResults.addAll(batch);

            if (batch.size() < chunkSize) {
                break;
            }

            lastId = idExtractor.apply(batch.getLast());
        }

        return allResults;
    }
}
