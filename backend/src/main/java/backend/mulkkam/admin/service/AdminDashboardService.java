package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.response.GetAdminDashboardStatsResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class AdminDashboardService {

    private final MemberRepository memberRepository;
    private final CupRepository cupRepository;
    private final DeviceRepository deviceRepository;
    private final IntakeHistoryRepository intakeHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final FriendRelationRepository friendRelationRepository;

    @Transactional(readOnly = true)
    public GetAdminDashboardStatsResponse getDashboardStats() {
        long totalMembers = memberRepository.count();
        long totalCups = cupRepository.count();
        long totalDevices = deviceRepository.count();
        long todayIntakeHistories = intakeHistoryRepository.countByHistoryDate(LocalDate.now());
        long totalNotifications = notificationRepository.count();
        long totalFriendRelations = friendRelationRepository.count();

        return new GetAdminDashboardStatsResponse(
                totalMembers,
                totalCups,
                totalDevices,
                todayIntakeHistories,
                totalNotifications,
                totalFriendRelations
        );
    }
}
