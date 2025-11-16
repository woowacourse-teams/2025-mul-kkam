package backend.mulkkam.friend.service;

import static org.assertj.core.api.Assertions.assertThat;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.domain.FriendReminderHistory;
import backend.mulkkam.friend.dto.request.CreateFriendReminderRequest;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.friend.repository.FriendReminderHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class FriendReminderHistoryServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private FriendReminderHistoryService friendReminderHistoryService;

    @Autowired
    private FriendReminderHistoryRepository friendReminderHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRelationRepository friendRelationRepository;

    private final Member sender = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("sender")).build();
    private final Member friend = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("friend")).build();

    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setup() {
        memberRepository.save(sender);
        memberRepository.save(friend);

        FriendRelation friendRelation = new FriendRelation(sender.getId(), friend.getId(), FriendRelationStatus.ACCEPTED);
        friendRelationRepository.save(friendRelation);

        FriendReminderHistory initialHistory = new FriendReminderHistory(sender.getId(), friend.getId(), TODAY);
        friendReminderHistoryRepository.save(initialHistory);
    }

    @Test
    @DisplayName("10개의 동시 요청이 모두 성공하고, 리마인더 횟수가 정확히 0으로 감소하는지 검증")
    void testCreateAndSendReminder_ConcurrentRequests() throws InterruptedException {
        // given
        final int CONCURRENT_REQUESTS = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_REQUESTS);

        CreateFriendReminderRequest request = new CreateFriendReminderRequest(friend.getId());
        MemberDetails memberDetails = new MemberDetails(sender);

        // when
        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            executorService.submit(() -> {
                try {
                    friendReminderHistoryService.createAndSendReminder(request, memberDetails);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        FriendReminderHistory resultHistory = friendReminderHistoryRepository.findBySenderIdAndRecipientIdAndQuotaDate(
                sender.getId(), friend.getId(), TODAY
        ).orElseThrow();
        assertThat(resultHistory.getRemaining()).isEqualTo((short) 0);
    }
}
