package backend.mulkkam.support.fixture;

import backend.mulkkam.device.domain.Device;
import backend.mulkkam.member.domain.Member;

public class DeviceFixtureBuilder {

    private final Member member;
    private String token = "token";
    private String deviceUuid = "deviceUuid";

    private DeviceFixtureBuilder(Member member) {
        this.member = member;
    }

    public static DeviceFixtureBuilder withMember(Member member) {
        return new DeviceFixtureBuilder(member);
    }

    public DeviceFixtureBuilder token(String token) {
        this.token = token;
        return this;
    }

    public DeviceFixtureBuilder deviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
        return this;
    }

    public Device build() {
        return new Device(
                token,
                deviceUuid,
                member
        );
    }

    public Device buildWithId(Long id) {
        return new Device(
                id,
                token,
                deviceUuid,
                member
        );
    }
}
