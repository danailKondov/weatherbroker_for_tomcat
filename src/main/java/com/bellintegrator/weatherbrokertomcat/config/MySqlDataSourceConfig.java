package com.bellintegrator.weatherbrokertomcat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

import javax.sql.DataSource;
import java.util.Properties;

@Profile("!dev")
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(
        basePackages = "com.bellintegrator.weatherbrokertomcat.dao",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
public class MySqlDataSourceConfig {

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
        com.mysql.jdbc.jdbc2.optional.MysqlXADataSource mySqlXADataSource = new MysqlXADataSource();
        mySqlXADataSource.setUrl(url);
        mySqlXADataSource.setPassword(passWord);
        mySqlXADataSource.setUser(userName);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mySqlXADataSource);
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
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        adapter.setGenerateDdl(true);
        return adapter;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    @DependsOn({"atomikosJtaPlatform"})
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setJpaVendorAdapter(jpaVendorAdapter());

        entityManager.setPackagesToScan("com.bellintegrator.weatherbrokertomcat.model");
        entityManager.setJtaDataSource(dataSource());
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.current_session_context_class", "jta");
        properties.put("hibernate.transaction.factory_class", "org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory");
        properties.put("hibernate.transaction.jta.platform", "com.bellintegrator.weatherbrokertomcat.config.AtomikosJtaPlatform");

        entityManager.setJpaProperties(properties);
        return entityManager;
    }
}
