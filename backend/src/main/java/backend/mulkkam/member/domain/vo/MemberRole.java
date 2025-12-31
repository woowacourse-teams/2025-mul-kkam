package backend.mulkkam.member.domain.vo;

public enum MemberRole {

    NONE,
    MEMBER,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
