package backend.mulkkam.intake.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class IntakeHistoryRepositoryTest {
    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;
}
