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
        String databaseName = getDatabaseName();

        if (isH2Database(databaseName)) {
            cleanH2Database();
        } else if (isMySQLDatabase(databaseName)) {
            cleanMySQLDatabase();
        } else {
            throw new UnsupportedOperationException("지원하지 않는 데이터베이스입니다: " + databaseName);
        }
    }

    private void cleanH2Database() {
        // H2에서 외래 키 제약조건 비활성화
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // H2용 테이블 목록 조회
        List<String> tableNames = entityManager.createNativeQuery(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                                "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'BASE TABLE'")
                .getResultList();

        // 테이블 정리
        for (String table : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }

        // 외래 키 제약조건 다시 활성화
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private void cleanMySQLDatabase() {
        // MySQL에서 외래 키 체크 비활성화
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // 현재 스키마 이름 가져오기
        String schema = (String) entityManager
                .createNativeQuery("SELECT DATABASE()")
                .getSingleResult();

        // MySQL용 테이블 목록 조회
        List<String> tableNames = entityManager.createNativeQuery(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                                "WHERE TABLE_SCHEMA = :schema AND TABLE_TYPE = 'BASE TABLE'")
                .setParameter("schema", schema)
                .getResultList();

        // 테이블 순회하여 TRUNCATE 및 AUTO_INCREMENT 초기화
        for (String table : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }

        // 외래 키 체크 다시 활성화
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    private String getDatabaseName() {
        try {
            // H2인지 확인
            entityManager.createNativeQuery("SELECT H2VERSION()").getSingleResult();
            return "H2";
        } catch (Exception e1) {
            try {
                // MySQL인지 확인
                entityManager.createNativeQuery("SELECT VERSION()").getSingleResult();
                return "MySQL";
            } catch (Exception e2) {
                // 다른 방법으로 데이터베이스 확인
                String url = entityManager.getEntityManagerFactory()
                        .getProperties()
                        .get("hibernate.connection.url")
                        .toString()
                        .toLowerCase();

                if (url.contains("h2")) {
                    return "H2";
                } else if (url.contains("mysql")) {
                    return "MySQL";
                } else {
                    return "UNKNOWN";
                }
            }
        }
    }

    private boolean isH2Database(String databaseName) {
        return "H2".equalsIgnoreCase(databaseName);
    }

    private boolean isMySQLDatabase(String databaseName) {
        return "MySQL".equalsIgnoreCase(databaseName);
    }
}
