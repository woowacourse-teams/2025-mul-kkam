package backend.mulkkam.cup.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixture;
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

        Cup cup1 = new CupFixture()
                .member(member)
                .cupRank(1)
                .build();
        Cup cup2 = new CupFixture()
                .member(member)
                .cupRank(2)
                .build();

        cupRepository.saveAll(List.of(cup1, cup2));

        // when
        List<Cup> cups = cupRepository.findAllByMemberId(member.getId());

        // then
        assertThat(cups).hasSize(2);
    }

    @Test
    @DisplayName("사용자 ID로 최대 랭크를 조회한다")
    void findMaxRankByMemberId() {
        // given
        Member member = new MemberFixture().build();
        memberRepository.save(member);

        Cup cup1 = new CupFixture()
                .member(member)
                .cupRank(1)
                .build();
        Cup cup2 = new CupFixture()
                .member(member)
                .cupRank(3)
                .build();

        cupRepository.saveAll(List.of(cup1, cup2));

        // when
        Optional<Integer> maxRank = cupRepository.findMaxRankByMemberId(member.getId());

        // then
        assertSoftly(softly -> {
            softly.assertThat(maxRank).isPresent();
            softly.assertThat(maxRank.get()).isEqualTo(3);
        });
    }
}
