package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(HikariDataSource.class)
public class PostgresDataSourceCustomizer implements BeanPostProcessor {

    private final DataSourceProperties dataSourceProperties;

    public PostgresDataSourceCustomizer(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof HikariDataSource dataSource && isPostgres(dataSource)) {
            dataSource.addDataSourceProperty("stringtype", "unspecified");
        }
        return bean;
    }

    private boolean isPostgres(HikariDataSource dataSource) {
        String driverClassName = dataSourceProperties.getDriverClassName();
        if (driverClassName != null) {
            return driverClassName.contains("postgresql");
        }
        String jdbcUrl = dataSourceProperties.determineUrl();
        if (jdbcUrl != null) {
            return jdbcUrl.startsWith("jdbc:postgresql:");
        }
        jdbcUrl = dataSource.getJdbcUrl();
        return jdbcUrl != null && jdbcUrl.startsWith("jdbc:postgresql:");
    }
}
