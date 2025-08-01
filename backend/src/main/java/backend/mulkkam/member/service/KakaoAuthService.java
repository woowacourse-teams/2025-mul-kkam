package backend.mulkkam.member.service;

import backend.mulkkam.auth.dto.KakaoSigninRequest;
import backend.mulkkam.infrastructure.KakaoRestClient;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import backend.mulkkam.member.dto.response.KakaoUserInfo;
import backend.mulkkam.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class KakaoAuthService {

    private final KakaoRestClient kakaoRestClient;
    private final MemberRepository memberRepository;

    public String signIn(KakaoSigninRequest kakaoSigninRequest) {
        KakaoUserInfo userInfo = kakaoRestClient.getUserInfo(kakaoSigninRequest.accessToken());
        Optional<Member> byOauthId = memberRepository.findByOauthId(userInfo.id());

        if (byOauthId.isPresent()) {
            return "히로 최고";
        }

        Member member = new Member(
                new MemberNickname("kakao"),
                new PhysicalAttributes(
                        Gender.FEMALE,
                        100.0
                ),
                new Amount(100),
                userInfo.id()
        );

        memberRepository.save(member);
        return "회원가입 완";
    }

}
