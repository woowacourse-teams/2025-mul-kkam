package backend.mulkkam.support;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.Type;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatabaseCleaner {

    private final EntityManager entityManager;

    public DatabaseCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void clean() {
        List<String> tableNames = entityManager.getMetamodel()
                .getEntities()
                .stream()
                .map(Type::getJavaType)
                .map(javaType -> {
                    Table table = javaType.getAnnotation(Table.class);
                    return (table != null && !table.name().isBlank())
                            ? table.name()
                            : toSnakeCase(javaType.getSimpleName());
                })
                .toList();

        entityManager.flush();
        entityManager.createNativeQuery("SET foreign_key_checks = 0").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        entityManager.createNativeQuery("SET foreign_key_checks = 1").executeUpdate();

    }

    private String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
