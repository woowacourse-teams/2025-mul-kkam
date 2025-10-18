package backend.mulkkam.friend.controller;

import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.friend.dto.PatchFriendStatusRequest.FriendRequestStatus.ACCEPT;
import static backend.mulkkam.friend.dto.PatchFriendStatusRequest.FriendRequestStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.dto.PatchFriendStatusRequest;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.List;

class FriendRequestControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private FriendRelationRepository friendRelationRepository;

    private Member requester;
    private Member addressee;
    private String tokenOfAddressee;

    @BeforeEach
    void setUp() {
        requester = MemberFixtureBuilder.builder().build();
        memberRepository.save(requester);

        addressee = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("칼리2")).build();
        memberRepository.save(addressee);

        OauthAccount oauthAccountOfRequester = new OauthAccount(requester, "testIdOfRequester",
                OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccountOfRequester);

        OauthAccount oauthAccountOfAddressee = new OauthAccount(addressee, "testIdOfAddressee",
                OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccountOfAddressee);

        tokenOfAddressee = oauthJwtTokenHandler.createAccessToken(oauthAccountOfAddressee, "temp");
    }

    @DisplayName("친구 요청 상태를 수정할 때")
    @Nested
    class UpdateFriendRequest {

        @DisplayName("정상적으로 거절된다")
        @Test
        void success_rejected() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);
            PatchFriendStatusRequest request = new PatchFriendStatusRequest(REJECT);

            // when
            mockMvc.perform(patch("/friend-requests/" + friendRelation.getId())
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friendRelations).hasSize(0);
            });
        }

        @DisplayName("정상적으로 수락된다")
        @Test
        void success_accepted() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);
            PatchFriendStatusRequest request = new PatchFriendStatusRequest(ACCEPT);

            // when
            mockMvc.perform(patch("/friend-requests/" + friendRelation.getId())
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION,
                                    "Bearer " + tokenOfAddressee)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friendRelations).hasSize(1);
                softly.assertThat(friendRelations.getFirst().getRequesterId()).isEqualTo(requester.getId());
                softly.assertThat(friendRelations.getFirst().getAddresseeId()).isEqualTo(addressee.getId());
                softly.assertThat(friendRelations.getFirst().getFriendRelationStatus())
                        .isEqualTo(FriendRelationStatus.ACCEPTED);
            });
        }

        @DisplayName("친구 신청 수신자가 아닌 멤버가 요청한 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"REJECT", "ACCEPT"})
        void error_acceptByOtherMember(String status) throws Exception {
            // given
            Member other = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("똥똥이"))
                    .build();
            memberRepository.save(other);

            OauthAccount oauthAccountOfOther = new OauthAccount(
                    other,
                    "testIdOfOther",
                    OauthProvider.KAKAO
            );
            oauthAccountRepository.save(oauthAccountOfOther);
            String tokenOfOther = oauthJwtTokenHandler.createAccessToken(oauthAccountOfOther, "other");

            FriendRelation friendRelation = new FriendRelation(
                    requester.getId(),
                    addressee.getId(),
                    FriendRelationStatus.REQUESTED
            );
            friendRelationRepository.save(friendRelation);
            PatchFriendStatusRequest request = new PatchFriendStatusRequest(
                    PatchFriendStatusRequest.FriendRequestStatus.valueOf(status)
            );

            // when
            String json = mockMvc.perform(patch("/friend-requests/" + friendRelation.getId())
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION,
                                    "Bearer " + tokenOfOther)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST.name());
        }
    }
}
