package backend.mulkkam.member.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.dto.PhysicalAttributesModifyRequest;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void modifyPhysicalAttributes(
            PhysicalAttributesModifyRequest physicalAttributesModifyRequest,
            Long memberId
    ) {
        Member member = getById(memberId);
        member.updatePhysicalAttributes(physicalAttributesModifyRequest.toPhysicalAttributes());
    }

    @Transactional
    public void checkForDuplicates(
            String nickname,
            Long id
    ) {
        Member member = getById(id);
        if (member.getMemberNickname().value().equals(nickname)) {
            throw new CommonException(SAME_AS_BEFORE_NICKNAME);
        }
        if (memberRepository.existsByMemberNicknameValue(nickname)) {
            throw new CommonException(DUPLICATE_MEMBER_NICKNAME);
        }
    }

    private Member getById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }
}
