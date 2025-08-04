package backend.mulkkam.member.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.request.MemberNicknameModifyRequest;
import backend.mulkkam.member.dto.request.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.dto.response.MemberNicknameResponse;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static backend.mulkkam.common.exception.errorCode.ErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.common.exception.errorCode.ErrorCode.SAME_AS_BEFORE_NICKNAME;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMemberById(long id) {
        Member member = getById(id);
        return new MemberResponse(member);
    }

    @Transactional
    public void modifyPhysicalAttributes(
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest,
            Long memberId
    ) {
        Member member = getById(memberId);
        member.updatePhysicalAttributes(physicalAttributesModifyRequest.toPhysicalAttributes());
    }

    public void validateDuplicateNickname(
            String nickname,
            Long memberId
    ) {
        Member member = getById(memberId);
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
            Long memberId
    ) {
        Member member = getById(memberId);
        member.updateNickname(memberNicknameModifyRequest.toMemberNickname());
    }

    public MemberNicknameResponse getNickname(Long memberId) {
        Member member = getById(memberId);
        return new MemberNicknameResponse(member.getMemberNickname());
    }

    private Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
