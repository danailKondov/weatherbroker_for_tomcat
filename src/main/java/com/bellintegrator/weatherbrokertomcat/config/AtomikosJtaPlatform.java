package com.bellintegrator.weatherbrokertomcat.config;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

public class AtomikosJtaPlatform extends AbstractJtaPlatform {

    private static UserTransaction ut;
    private static TransactionManager tm;

    @Override
    protected TransactionManager locateTransactionManager() {
        return tm;
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return ut;
    }

    public static UserTransaction getUt() {
        return ut;
    }

    public static void setUt(UserTransaction ut) {
        AtomikosJtaPlatform.ut = ut;
    }

    public static TransactionManager getTm() {
        return tm;
    }

    public static void setTm(TransactionManager tm) {
        AtomikosJtaPlatform.tm = tm;
    }
}
