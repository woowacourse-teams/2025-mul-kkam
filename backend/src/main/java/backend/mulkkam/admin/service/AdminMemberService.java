package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.request.UpdateAdminMemberRequest;
import backend.mulkkam.admin.dto.response.GetAdminMemberDetailResponse;
import backend.mulkkam.admin.dto.response.GetAdminMemberListResponse;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.MemberRole;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public Page<GetAdminMemberListResponse> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(GetAdminMemberListResponse::from);
    }

    public GetAdminMemberDetailResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
        return GetAdminMemberDetailResponse.from(member);
    }

    @Transactional
    public GetAdminMemberDetailResponse updateMember(
            Long memberId,
            UpdateAdminMemberRequest request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));

        member.updateNickname(new MemberNickname(request.nickname()));
        member.updatePhysicalAttributes(new PhysicalAttributes(request.gender(), request.weight()));
        member.updateTargetAmount(new TargetAmount(request.targetAmount()));
        member.modifyIsMarketingNotificationAgreed(request.isMarketingNotificationAgreed());
        member.modifyIsNightNotificationAgreed(request.isNightNotificationAgreed());
        member.modifyIsReminderEnabled(request.isReminderEnabled());
        member.modifyMemberRole(request.memberRole());

        return GetAdminMemberDetailResponse.from(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        memberService.delete(new MemberDetails(memberId, MemberRole.MEMBER));
    }
}
