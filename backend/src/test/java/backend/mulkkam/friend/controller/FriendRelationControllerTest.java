package backend.mulkkam.friend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendStatus;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class FriendRelationControllerTest extends ControllerTest {

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
    private String tokenOfRequester;
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

        tokenOfRequester = oauthJwtTokenHandler.createAccessToken(oauthAccountOfRequester, "temp");
        tokenOfAddressee = oauthJwtTokenHandler.createAccessToken(oauthAccountOfAddressee, "temp");
    }

    @DisplayName("친구 목록에서 친구를 삭제할 때")
    @Nested
    class DeleteFriendRelation {

        @DisplayName("요청자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByRequester() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendStatus.ACCEPTED);
            FriendRelation savedFriendRelation = friendRelationRepository.save(friendRelation);

            // when
            mockMvc.perform(delete("/friends/" + savedFriendRelation.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andExpect(status().isOk());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertThat(friendRelations).isEmpty();
        }

        @DisplayName("수락자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByAddressee() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendStatus.ACCEPTED);
            friendRelationRepository.save(friendRelation);
            FriendRelation savedFriendRelation = friendRelationRepository.save(friendRelation);

            // when
            mockMvc.perform(delete("/friends/" + savedFriendRelation.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friendRelations).hasSize(0);
            });
        }

        @DisplayName("친구 요청을 거절할 때")
        @Nested
        class RejectFriendRelationRequest {

            @DisplayName("정상적으로 거절된다")
            @Test
            void success_rejected() throws Exception {
                // given
                FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                        FriendStatus.REQUESTED);
                friendRelationRepository.save(friendRelation);

                // when
                mockMvc.perform(post("/friends/request/" + friendRelation.getId() + "/reject")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                        .andDo(print()).andExpect(status().isOk());

                // then
                List<FriendRelation> friendRelations = friendRelationRepository.findAll();
                assertSoftly(softly -> {
                    softly.assertThat(friendRelations).hasSize(0);
                });
            }
        }

        @DisplayName("친구 요청을 수락할 때")
        @Nested
        class AcceptFriendRelationRequest {

            @DisplayName("정상적으로 수락된다")
            @Test
            void success_accepted() throws Exception {
                // given
                FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                        FriendStatus.REQUESTED);
                friendRelationRepository.save(friendRelation);

                // when
                mockMvc.perform(post("/friends/request/" + friendRelation.getId() + "/accept")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + tokenOfAddressee))
                        .andDo(print()).andExpect(status().isOk());

                // then
                List<FriendRelation> friendRelations = friendRelationRepository.findAll();
                assertSoftly(softly -> {
                    softly.assertThat(friendRelations).hasSize(1);
                    softly.assertThat(friendRelations.getFirst().getRequesterId()).isEqualTo(requester.getId());
                    softly.assertThat(friendRelations.getFirst().getAddresseeId()).isEqualTo(addressee.getId());
                    softly.assertThat(friendRelations.getFirst().getFriendStatus()).isEqualTo(FriendStatus.ACCEPTED);
                });
            }
        }
    }
}
