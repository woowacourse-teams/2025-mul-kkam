package backend.mulkkam.friend.controller;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.EXCEED_FRIEND_REMINDER_LIMIT;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.dto.request.CreateFriendReminderRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.List;

class FriendControllerTest extends ControllerTest {

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
                    FriendRelationStatus.ACCEPTED);
            friendRelationRepository.save(friendRelation);

            // when
            mockMvc.perform(delete("/friends?memberId=" + addressee.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andExpect(status().isNoContent());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertThat(friendRelations).isEmpty();
        }

        @DisplayName("수락자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByAddressee() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.ACCEPTED);
            friendRelationRepository.save(friendRelation);

            // when
            mockMvc.perform(delete("/friends?memberId=" + requester.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isNoContent());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertThat(friendRelations).isEmpty();
        }
    }

    @DisplayName("친구에게 물풍선을 보낼 때")
    @Nested
    class CreateReminder {

        @DisplayName("친구 관계가 정상적으로 확인되면 리마인더를 전송한다")
        @Test
        void success_whenValidFriend() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.ACCEPTED);
            friendRelationRepository.save(friendRelation);

            CreateFriendReminderRequest request = new CreateFriendReminderRequest(addressee.getId());

            // when & then
            mockMvc.perform(post("/friends/reminder")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }

        @DisplayName("친구 관계가 아닌 경우 예외가 발생한다")
        @Test
        void fail_whenNotFriends() throws Exception {
            // given
            CreateFriendReminderRequest request = new CreateFriendReminderRequest(addressee.getId());

            // when & then
            String json = mockMvc.perform(post("/friends/reminder")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            FailureBody errorResponse = objectMapper.readValue(json, FailureBody.class);
            assertThat(errorResponse.getCode()).isEqualTo(NOT_FOUND_FRIEND.name());
        }

        @DisplayName("일일 전송 횟수를 초과하면 예외가 발생한다")
        @Test
        void fail_whenExceedDailyLimit() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.ACCEPTED);
            friendRelationRepository.save(friendRelation);

            CreateFriendReminderRequest request = new CreateFriendReminderRequest(addressee.getId());

            // 10번 성공적으로 전송
            for (int i = 0; i < 10; i++) {
                mockMvc.perform(post("/friends/reminder")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isNoContent());
            }

            // when & then - 11번째 시도는 실패
            String json = mockMvc.perform(post("/friends/reminder")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            FailureBody errorResponse = objectMapper.readValue(json, FailureBody.class);
            assertThat(errorResponse.getCode()).isEqualTo(EXCEED_FRIEND_REMINDER_LIMIT.name());
        }

        @DisplayName("유효하지 않은 요청 데이터인 경우 예외가 발생한다")
        @Test
        void fail_whenInvalidRequest() throws Exception {
            // given - memberId가 null인 잘못된 요청
            String invalidRequest = "{\"memberId\": null}";

            // when & then
            mockMvc.perform(post("/friends/reminder")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .contentType(APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest());
        }
    }
}
