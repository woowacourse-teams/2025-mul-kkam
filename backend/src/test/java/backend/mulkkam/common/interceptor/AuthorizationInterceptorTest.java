package backend.mulkkam.common.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.mulkkam.common.auth.annotation.AuthLevel;
import backend.mulkkam.common.auth.annotation.RequireAuth;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.dto.OauthAccountDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode;
import backend.mulkkam.member.domain.vo.MemberRole;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorizationInterceptor 단위 테스트")
class AuthorizationInterceptorTest {

    @InjectMocks
    private AuthorizationInterceptor authorizationInterceptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @DisplayName("어노테이션이 없는 경우")
    @Nested
    class WhenNoAnnotation {

        @DisplayName("true를 반환한다")
        @Test
        void returnTrue() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("noAnnotation");

            // when
            boolean result = authorizationInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();
        }
    }

    @DisplayName("AuthLevel.NONE인 경우")
    @Nested
    class WhenAuthLevelIsNone {

        @DisplayName("true를 반환한다")
        @Test
        void returnTrue() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("noneLevel");

            // when
            boolean result = authorizationInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();
        }
    }

    @DisplayName("AuthLevel.ACCOUNT인 경우")
    @Nested
    class WhenAuthLevelIsAccount {

        @DisplayName("account_id가 없으면 CommonException을 던진다")
        @Test
        void throwCommonException_whenAccountIdIsNull() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("accountLevel");
            request.setAttribute("account_id", null);
            request.setAttribute("device_uuid", "device_uuid");

            // when & then
            assertThatThrownBy(() -> authorizationInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }

        @DisplayName("device_uuid가 없으면 CommonException을 던진다")
        @Test
        void throwCommonException_whenDeviceUuidIsNull() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("accountLevel");
            request.setAttribute("account_id", 1L);
            request.setAttribute("device_uuid", null);

            // when & then
            assertThatThrownBy(() -> authorizationInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }

        @DisplayName("Account가 있으면 oauthAccountDetails를 주입하고 true를 반환한다")
        @Test
        void injectOauthAccountDetailsAndReturnTrue_whenAccountExists() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("accountLevel");
            request.setAttribute("account_id", 1L);
            request.setAttribute("device_uuid", "device_uuid");

            // when
            boolean result = authorizationInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();

            OauthAccountDetails oauthAccountDetails = (OauthAccountDetails) request.getAttribute(
                    "oauth_account_details");
            assertThat(oauthAccountDetails).isNotNull();
            assertThat(oauthAccountDetails.id()).isEqualTo(1L);
            assertThat(oauthAccountDetails.deviceUuid()).isEqualTo("device_uuid");
        }
    }

    @DisplayName("AuthLevel.MEMBER인 경우")
    @Nested
    class WhenAuthLevelIsMember {

        @DisplayName("member_id가 없으면 CommonException을 던진다")
        @Test
        void throwCommonException_whenMemberIdIsNull() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("memberLevel");
            request.setAttribute("member_id", null);
            request.setAttribute("member_role", MemberRole.NONE);

            // when & then
            assertThatThrownBy(() -> authorizationInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }

        @DisplayName("member_role이 NONE이면 CommonException을 던진다")
        @Test
        void throwCommonException_whenMemberRoleIsNone() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("memberLevel");
            request.setAttribute("member_id", 1L);
            request.setAttribute("member_role", MemberRole.NONE);

            // when & then
            assertThatThrownBy(() -> authorizationInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }

        @DisplayName("Member가 있으면 memberDetails를 주입하고 true를 반환한다")
        @Test
        void injectMemberDetailsAndReturnTrue_whenMemberExists() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("memberLevel");
            request.setAttribute("member_id", 1L);
            request.setAttribute("member_role", MemberRole.MEMBER);

            // when
            boolean result = authorizationInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();

            MemberDetails memberDetails = (MemberDetails) request.getAttribute("member_details");
            assertThat(memberDetails).isNotNull();
            assertThat(memberDetails.id()).isEqualTo(1L);
            assertThat(memberDetails.memberRole()).isEqualTo(MemberRole.MEMBER);
        }
    }

    @DisplayName("AuthLevel.ADMIN인 경우")
    @Nested
    class WhenAuthLevelIsAdmin {

        @DisplayName("member_id가 없으면 CommonException을 던진다")
        @Test
        void throwCommonException_whenMemberIdIsNull() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("adminLevel");
            request.setAttribute("member_id", null);
            request.setAttribute("member_role", MemberRole.ADMIN);

            // when & then
            assertThatThrownBy(() -> authorizationInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }

        @DisplayName("일반 회원이면 CommonException을 던진다")
        @Test
        void throwCommonException_whenMemberIsNotAdmin() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("adminLevel");
            request.setAttribute("member_id", 1L);
            request.setAttribute("member_role", MemberRole.MEMBER);

            // when & then
            assertThatThrownBy(() -> authorizationInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }

        @DisplayName("관리자이면 memberDetails를 주입하고 true를 반환한다")
        @Test
        void injectMemberDetailsAndReturnTrue_whenMemberIsAdmin() throws Exception {
            // given
            HandlerMethod handlerMethod = createHandlerMethod("adminLevel");
            request.setAttribute("member_id", 1L);
            request.setAttribute("member_role", MemberRole.ADMIN);

            // when
            boolean result = authorizationInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();

            MemberDetails memberDetails = (MemberDetails) request.getAttribute("member_details");
            assertThat(memberDetails).isNotNull();
            assertThat(memberDetails.id()).isEqualTo(1L);
            assertThat(memberDetails.memberRole()).isEqualTo(MemberRole.ADMIN);
            assertThat(memberDetails.isAdmin()).isTrue();
        }
    }

    private HandlerMethod createHandlerMethod(String methodName) throws NoSuchMethodException {
        Method method = TestController.class.getMethod(methodName);
        TestController controller = new TestController();
        return new HandlerMethod(controller, method);
    }

    static class TestController {

        public void noAnnotation() {
        }

        @RequireAuth(level = AuthLevel.NONE)
        public void noneLevel() {
        }

        @RequireAuth(level = AuthLevel.ACCOUNT)
        public void accountLevel() {
        }

        @RequireAuth(level = AuthLevel.MEMBER)
        public void memberLevel() {
        }

        @RequireAuth(level = AuthLevel.ADMIN)
        public void adminLevel() {
        }
    }
}
