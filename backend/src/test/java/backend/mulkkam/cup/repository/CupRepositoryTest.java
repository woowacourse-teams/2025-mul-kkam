package backend.mulkkam.cup.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixture;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("사용자 ID로 사용자 전체 컵을 조회한다")
    void findAllByMemberId() {
        // given
        Member member = new MemberFixture().build();
        memberRepository.save(member);

        Cup cup1 = new Cup(member, new CupNickname("컵1"), 300, 1);
        Cup cup2 = new Cup(member, new CupNickname("컵2"), 500, 2);
        cupRepository.saveAll(List.of(cup1, cup2));

        // when
        List<Cup> cups = cupRepository.findAllByMemberId(member.getId());

        // then
        assertThat(cups).hasSize(2);
    }

    @Test
    @DisplayName("사용자 ID로 사용자 전체 컵을 조회한다")
    void findMaxRankByMemberId() {
        // given
        Member member = new MemberFixture().build();
        memberRepository.save(member);

        cupRepository.save(new Cup(member, new CupNickname("컵1"), 400, 1));
        cupRepository.save(new Cup(member, new CupNickname("컵3"), 400, 3));

        // when
        Optional<Integer> maxRank = cupRepository.findMaxRankByMemberId(member.getId());

        // then
        assertSoftly(softly -> {
            softly.assertThat(maxRank.isPresent()).isTrue();
            softly.assertThat(maxRank.get()).isEqualTo(3);
        });
    }
}
