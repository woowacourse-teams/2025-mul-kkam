package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.dto.CreateActivityNotification;
import backend.mulkkam.notification.dto.CreateTokenSuggestionNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ActivityService {

    private final SuggestionNotificationService suggestionNotificationService;
    private final MemberRepository memberRepository;

    public void createActivityNotification(
            CreateActivityNotification createActivityNotification,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        CreateTokenSuggestionNotificationRequest createTokenSuggestionNotificationRequest = createActivityNotification.toFcmToken(member);
        suggestionNotificationService.createAndSendSuggestionNotification(createTokenSuggestionNotificationRequest);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
