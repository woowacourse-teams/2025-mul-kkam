package backend.mulkkam.notification.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationBatchServiceUnitTest {

    @InjectMocks
    private NotificationBatchService notificationBatchService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EntityManager entityManager;

    @DisplayName("배치로 알림을 처리할 때")
    @Nested
    class ProcessOneChunk {

        private final NotificationMessageTemplate template = new NotificationMessageTemplate(
                "제목",
                "내용",
                Action.GO_HOME,
                NotificationType.REMIND
        );

        @DisplayName("멤버 ID 리스트로부터 알림을 생성하고 저장한다")
        @Test
        void success_whenValidMemberIds() {
            // given
            List<Long> memberIds = List.of(1L, 2L, 3L);
            LocalDateTime now = LocalDateTime.of(2025, 1, 15, 14, 30);
            
            Member member1 = MemberFixtureBuilder.builder().buildWithId(1L);
            Member member2 = MemberFixtureBuilder.builder().buildWithId(2L);
            Member member3 = MemberFixtureBuilder.builder().buildWithId(3L);

            when(entityManager.getReference(Member.class, 1L)).thenReturn(member1);
            when(entityManager.getReference(Member.class, 2L)).thenReturn(member2);
            when(entityManager.getReference(Member.class, 3L)).thenReturn(member3);

            // when
            notificationBatchService.processOneChunk(template, now, memberIds);

            // then
            verify(notificationRepository).saveAll(
                    argThat(notifications -> {
                        List<Notification> notificationList = (List<Notification>) notifications;
                        return notificationList.size() == 3 &&
                                notificationList.stream().allMatch(n ->
                                        n.getNotificationType() == NotificationType.REMIND &&
                                        n.getContent().equals("내용") &&
                                        n.getCreatedAt().equals(now));
                    })
            );
            verify(entityManager).flush();
            verify(entityManager).clear();
        }

        @DisplayName("멤버 ID가 빈 리스트면 아무것도 저장하지 않는다")
        @Test
        void success_whenEmptyMemberIds() {
            // given
            List<Long> emptyMemberIds = List.of();
            LocalDateTime now = LocalDateTime.of(2025, 1, 15, 14, 30);

            // when
            notificationBatchService.processOneChunk(template, now, emptyMemberIds);

            // then
            verify(notificationRepository, never()).saveAll(any());
            verify(entityManager, never()).flush();
            verify(entityManager, never()).clear();
        }

        @DisplayName("단일 멤버로도 정상적으로 처리된다")
        @Test
        void success_whenSingleMemberId() {
            // given
            List<Long> singleMemberId = List.of(1L);
            LocalDateTime now = LocalDateTime.of(2025, 1, 15, 14, 30);
            
            Member member = MemberFixtureBuilder.builder().buildWithId(1L);
            when(entityManager.getReference(Member.class, 1L)).thenReturn(member);

            // when
            notificationBatchService.processOneChunk(template, now, singleMemberId);

            // then
            verify(notificationRepository).saveAll(
                    argThat(notifications -> {
                        List<Notification> notificationList = (List<Notification>) notifications;
                        return notificationList.size() == 1;
                    })
            );
            verify(entityManager).flush();
            verify(entityManager).clear();
        }

        @DisplayName("엔티티 매니저를 사용하여 대량 처리 시 메모리를 관리한다")
        @Test
        void success_memoryManagement() {
            // given
            List<Long> memberIds = List.of(1L, 2L, 3L, 4L, 5L);
            LocalDateTime now = LocalDateTime.of(2025, 1, 15, 14, 30);
            
            for (Long id : memberIds) {
                Member member = MemberFixtureBuilder.builder().buildWithId(id);
                when(entityManager.getReference(Member.class, id)).thenReturn(member);
            }

            // when
            notificationBatchService.processOneChunk(template, now, memberIds);

            // then
            verify(entityManager).flush();
            verify(entityManager).clear();
        }
    }
}