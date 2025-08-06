package backend.mulkkam.member.service;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.OnboardingStatusResponse;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse get(Member member) {
        return new MemberResponse(member);
    }

    @Transactional
    public void modifyPhysicalAttributes(
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest,
            Member member
    ) {
        member.updatePhysicalAttributes(physicalAttributesModifyRequest.toPhysicalAttributes());
        memberRepository.save(member);
    }

    public void validateDuplicateNickname(
            String nickname,
            Member member
    ) {
        if (member.isSameNickname(new MemberNickname(nickname))) {
            throw new CommonException(SAME_AS_BEFORE_NICKNAME);
        }
        if (memberRepository.existsByMemberNicknameValue(nickname)) {
            throw new CommonException(DUPLICATE_MEMBER_NICKNAME);
        }
    }

    @Transactional
    public void modifyNickname(
            MemberNicknameModifyRequest memberNicknameModifyRequest,
            Member member
    ) {
        member.updateNickname(memberNicknameModifyRequest.toMemberNickname());
        memberRepository.save(member);
    }

    public MemberNicknameResponse getNickname(Member member) {
        return new MemberNicknameResponse(member.getMemberNickname());
    }

    @Transactional
    public void create(
            OauthAccount oauthAccount,
            CreateMemberRequest createMemberRequest
    ) {
        Member member = createMemberRequest.toMember();
        memberRepository.save(member);

        oauthAccount.modifyMember(member);
    }

    public OnboardingStatusResponse checkOnboardingStatus(OauthAccount oauthAccount) {
        boolean finishedOnboarding = oauthAccount.finishedOnboarding();
        return new OnboardingStatusResponse(finishedOnboarding);
    }
}
