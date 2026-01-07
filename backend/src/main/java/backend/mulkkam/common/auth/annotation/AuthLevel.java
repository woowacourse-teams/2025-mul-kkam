package backend.mulkkam.common.auth.annotation;

import backend.mulkkam.common.auth.AuthContext;

public enum AuthLevel {


    NONE {
        @Override
        public boolean authorize(AuthContext authContext) {
            return true;
        }
    },
    ACCOUNT {
        @Override
        public boolean authorize(AuthContext authContext) {
            return authContext.requireAccount();
        }
    },
    MEMBER {
        @Override
        public boolean authorize(AuthContext authContext) {
            return authContext.requireMember();
        }
    },
    ADMIN {
        @Override
        public boolean authorize(AuthContext authContext) {
            return authContext.requireAdmin();
        }
    };

    public abstract boolean authorize(AuthContext authContext);
}
