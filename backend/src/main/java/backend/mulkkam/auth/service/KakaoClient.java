package backend.mulkkam.auth.service;

import backend.mulkkam.member.dto.response.KakaoUserInfo;

public interface KakaoClient {

    KakaoUserInfo getUserInfo(String accessToken);
}
