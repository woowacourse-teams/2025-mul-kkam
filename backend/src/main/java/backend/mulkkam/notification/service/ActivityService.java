package backend.mulkkam.notification.service;

import backend.mulkkam.averageTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.dto.CreateActivityNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

@RequiredArgsConstructor
@Service
public class ActivityService {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    public void createActivityNotification(
            CreateActivityNotification createActivityNotification,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        CreateTokenNotificationRequest createTokenNotificationRequest = createActivityNotification.toFcmToken(member);
        notificationService.createTokenNotification(createTokenNotificationRequest);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
