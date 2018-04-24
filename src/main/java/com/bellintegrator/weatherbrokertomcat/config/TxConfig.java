package com.bellintegrator.weatherbrokertomcat.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;

@Configuration
public class TxConfig {

    private final Logger log = LoggerFactory.getLogger(TxConfig.class);

    @Bean
    public UserTransactionImp atomikosUserTransaction() throws SystemException {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }

    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() {
        UserTransactionManager manager = new UserTransactionManager();
        manager.setForceShutdown(true);
        return manager;
    }

    @Bean
    @DependsOn({"atomikosUserTransaction", "atomikosTransactionManager"})
    public PlatformTransactionManager transactionManager() throws SystemException {
        JtaTransactionManager transactionManager = new JtaTransactionManager();
        transactionManager.setTransactionManager(atomikosTransactionManager());
        transactionManager.setUserTransaction(atomikosUserTransaction());
        transactionManager.setAllowCustomIsolationLevels(true);
        return transactionManager;
    }

    @Bean(name = "atomikosJtaPlatform")
    public AtomikosJtaPlatform atomikosJtaPlatform() {
        AtomikosJtaPlatform atomikosJtaPlatfom = new AtomikosJtaPlatform();
        try {
            atomikosJtaPlatfom.setTm(atomikosTransactionManager());
            atomikosJtaPlatfom.setUt(atomikosUserTransaction());
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
        return atomikosJtaPlatfom;
    }
}
