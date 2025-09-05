package backend.mulkkam.support.fixture;

import backend.mulkkam.device.domain.Device;
import backend.mulkkam.member.domain.Member;

public class DeviceFixtureBuilder {

    private final Member member;
    private String token = "token";
    private String deviceId = "deviceId";

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

    public DeviceFixtureBuilder deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public Device build() {
        return new Device(
                token,
                deviceId,
                member
        );
    }
}
