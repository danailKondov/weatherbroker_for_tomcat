package com.bellintegrator.weatherbrokertomcat.config;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(
        basePackages = "com.bellintegrator.weatherbrokertomcat.dao",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
public class H2DataSourceConfig {

    @Value("${spring.datasource.driverClassName}")
    String driverClass;
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String userName;
    @Value("${spring.datasource.password}")
    String passWord;

    @Bean
    @Primary
    public DataSource dataSource() {
//        System.out.println("dataSource init");
        JdbcDataSource h2xaDataSource = new JdbcDataSource();
        h2xaDataSource.setUrl(url);
        h2xaDataSource.setPassword(passWord);
        h2xaDataSource.setUser(userName);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(h2xaDataSource);
        xaDataSource.setUniqueResourceName("dataSource");
        xaDataSource.setMinPoolSize(10);
        xaDataSource.setPoolSize(10);
        xaDataSource.setMaxPoolSize(30);
        xaDataSource.setBorrowConnectionTimeout(60);
        xaDataSource.setReapTimeout(20);
        xaDataSource.setMaxIdleTime(60);
        xaDataSource.setMaintenanceInterval(60);
        return xaDataSource;
    }

    @Bean(name = "jpaVendorAdapter")
    public JpaVendorAdapter jpaVendorAdapter() {
//        System.out.println("jpaVendorAdapter init");
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setDatabase(Database.H2);
        adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        adapter.setGenerateDdl(true);
        return adapter;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    @DependsOn({"atomikosJtaPlatform"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        System.out.println("entityManagerFactory init");
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setJpaVendorAdapter(jpaVendorAdapter());

        entityManager.setPackagesToScan("com.bellintegrator.weatherbrokertomcat.model");
        entityManager.setJtaDataSource(dataSource());
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.current_session_context_class", "jta");
        properties.put("hibernate.transaction.factory_class", "org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory");
        properties.put("hibernate.transaction.jta.platform", "com.bellintegrator.weatherbrokertomcat.config.AtomikosJtaPlatform");

        entityManager.setJpaProperties(properties);
        return entityManager;
    }
}
