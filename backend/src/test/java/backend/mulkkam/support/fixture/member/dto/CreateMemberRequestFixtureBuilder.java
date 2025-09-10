package backend.mulkkam.support.fixture.member.dto;

import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.dto.CreateMemberRequest;
import java.util.List;

public class CreateMemberRequestFixtureBuilder {

    private String memberNickname;
    private Double weight;
    private Gender gender;
    private int targetAmount;
    private boolean isMarketingNotificationAgreed;
    private boolean isNightNotificationAgreed;
    private final List<CreateCupRequest> createCupRequests;

    private CreateMemberRequestFixtureBuilder(List<CreateCupRequest> createCupRequests) {
        this.memberNickname = "칼리";
        this.gender = Gender.FEMALE;
        this.weight = 50.2;
        this.targetAmount = 1_000;
        this.isMarketingNotificationAgreed = false;
        this.isNightNotificationAgreed = false;
        this.createCupRequests = createCupRequests;
    }

    private CreateMemberRequestFixtureBuilder(Member member, List<CreateCupRequest> createCupRequests) {
        this.memberNickname = member.getActiveNickname();
        this.gender = member.getPhysicalAttributes().getGender();
        this.weight = member.getPhysicalAttributes().getWeight();
        this.targetAmount = member.getTargetAmount().value();
        this.isMarketingNotificationAgreed = member.isMarketingNotificationAgreed();
        this.isNightNotificationAgreed = member.isNightNotificationAgreed();
        this.createCupRequests = createCupRequests;
    }

    public static CreateMemberRequestFixtureBuilder withCreateCupRequests(List<CreateCupRequest> createCupRequests) {
        return new CreateMemberRequestFixtureBuilder(createCupRequests);
    }

    public static CreateMemberRequestFixtureBuilder withMemberAndCreateCupRequests(
            Member member,
            List<CreateCupRequest> createCupRequests
    ) {
        return new CreateMemberRequestFixtureBuilder(member, createCupRequests);
    }

    public CreateMemberRequestFixtureBuilder memberNickname(String memberNickname) {
        this.memberNickname = memberNickname;
        return this;
    }

    public CreateMemberRequestFixtureBuilder gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public CreateMemberRequestFixtureBuilder weight(Double weight) {
        this.weight = weight;
        return this;
    }

    public CreateMemberRequestFixtureBuilder targetAmount(int targetAmount) {
        this.targetAmount = targetAmount;
        return this;
    }

    public CreateMemberRequestFixtureBuilder isMarketingNotificationAgreed(boolean isMarketingNotificationAgreed) {
        this.isMarketingNotificationAgreed = isMarketingNotificationAgreed;
        return this;
    }

    public CreateMemberRequestFixtureBuilder isNightNotificationAgreed(boolean isNightNotificationAgreed) {
        this.isNightNotificationAgreed = isNightNotificationAgreed;
        return this;
    }

    public CreateMemberRequest build() {
        return new CreateMemberRequest(
                this.memberNickname,
                this.weight,
                this.gender,
                this.targetAmount,
                this.isMarketingNotificationAgreed,
                this.isNightNotificationAgreed,
                this.createCupRequests
        );
    }
}
