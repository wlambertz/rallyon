package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.stereotype.Component

@Component
@ConditionalOnClass(HikariDataSource::class)
class PostgresDataSourceCustomizer(
    private val dataSourceProperties: DataSourceProperties
) : BeanPostProcessor {

    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is HikariDataSource && isPostgres(bean)) {
            bean.addDataSourceProperty("stringtype", "unspecified")
        }
        return bean
    }

    private fun isPostgres(dataSource: HikariDataSource): Boolean {
        val driverClassName = dataSourceProperties.driverClassName
        if (driverClassName != null) {
            return driverClassName.contains("postgresql")
        }
        var jdbcUrl = nullableString(dataSourceProperties.determineUrl())
        if (jdbcUrl != null) {
            return jdbcUrl.startsWith("jdbc:postgresql:")
        }
        jdbcUrl = nullableString(dataSource.jdbcUrl)
        return jdbcUrl != null && jdbcUrl.startsWith("jdbc:postgresql:")
    }

    private fun nullableString(value: String?): String? = value
}
