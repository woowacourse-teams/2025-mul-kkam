package backend.mulkkam.support;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    private final EntityManager entityManager;

    public DatabaseCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void truncateAllTables() {
        // H2에서 외래 키 제약조건 비활성화
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // H2용 테이블 목록 조회
        List<String> tableNames = entityManager.createNativeQuery(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                                "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'BASE TABLE'")
                .getResultList();

        // 테이블 순회하여 TRUNCATE 수행
        for (String table : tableNames) {
            // H2에서는 TRUNCATE를 사용하는 것이 더 효율적
            entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }

        // 외래 키 제약조건 다시 활성화
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
