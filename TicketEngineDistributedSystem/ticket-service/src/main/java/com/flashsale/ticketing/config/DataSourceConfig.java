package com.flashsale.ticketing.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.flashsale.ticketing.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class DataSourceConfig {

    // --- Shard 1 ---
    @Bean(name = "shard1Primary")
    @ConfigurationProperties(prefix = "datasource.shard1")
    public DataSource shard1Primary() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "shard1Replica")
    @ConfigurationProperties(prefix = "datasource.shard1-replica")
    public DataSource shard1Replica() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    // --- Shard 2 ---
    @Bean(name = "shard2Primary")
    @ConfigurationProperties(prefix = "datasource.shard2")
    public DataSource shard2Primary() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "shard2Replica")
    @ConfigurationProperties(prefix = "datasource.shard2-replica")
    public DataSource shard2Replica() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    // --- Router ---
    @Bean
    public DataSource routingDataSource(
            @Qualifier("shard1Primary") DataSource s1Primary,
            @Qualifier("shard1Replica") DataSource s1Replica,
            @Qualifier("shard2Primary") DataSource s2Primary,
            @Qualifier("shard2Replica") DataSource s2Replica
    ) {
        TicketRoutingDataSource routingDataSource = new TicketRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();

        dataSourceMap.put("SHARD_1_PRIMARY", s1Primary);
        dataSourceMap.put("SHARD_1_REPLICA", s1Replica);
        dataSourceMap.put("SHARD_2_PRIMARY", s2Primary);
        dataSourceMap.put("SHARD_2_REPLICA", s2Replica);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(s1Primary);

        return routingDataSource;
    }


    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.flashsale.ticketing.model");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}