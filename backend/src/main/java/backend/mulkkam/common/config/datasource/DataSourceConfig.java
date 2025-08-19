package backend.mulkkam.common.config.datasource;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.ro")
    public DataSource roDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.rw")
    public DataSource rwDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public DataSource routingDataSource(
            @Qualifier("roDataSource") DataSource ro,
            @Qualifier("rwDataSource") DataSource rw
    ) {
        Map<Object, Object> targets = new HashMap<>();
        targets.put(DatabaseRole.READ_ONLY, ro);
        targets.put(DatabaseRole.READ_WRITE, rw);

        ReadWriteRoutingDataSource ds = new ReadWriteRoutingDataSource();
        ds.setTargetDataSources(targets);
        ds.setDefaultTargetDataSource(rw);
        return ds;
    }
}
