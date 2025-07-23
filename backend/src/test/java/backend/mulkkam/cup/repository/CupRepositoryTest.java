package backend.mulkkam.cup.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixture;
import backend.mulkkam.support.MemberFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CupRepositoryTest {

    @Autowired
    private CupRepository cupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("사용자 ID로 사용자 전체 컵을 조회한다")
    @Test
    void findAllByMemberId() {
        // given
        Member member = new MemberFixture().build();
        memberRepository.save(member);

        Cup cup1 = new CupFixture()
                .member(member)
                .cupRank(new CupRank(2))
                .build();
        Cup cup2 = new CupFixture()
                .member(member)
                .cupRank(new CupRank(1))
                .build();

        cupRepository.saveAll(List.of(cup1, cup2));

        // when
        List<Cup> cups = cupRepository.findAllByMemberIdOrderByCupRankAsc(member.getId());

        // then
        assertSoftly(softly -> {
            softly.assertThat(cups.getFirst().getMember()).isEqualTo(member);
            softly.assertThat(cups.getFirst().getCupRank().value()).isEqualTo(1);
            softly.assertThat(cups).hasSize(2);
        });
    }
}
