package org.example.amt.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class AccountModule extends AbstractModule {
    private static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;" +
            "INIT=runscript from 'classpath:create.sql'\\;runscript from 'classpath:init.sql'";
    private final Logger log = LoggerFactory.getLogger(AbstractModule.class);

    @Override
    protected void configure() {
        bind(AccountService.class).to(DefaultAccountService.class);
        bind(AccountSettingsService.class).to(HardcodedAccountSettingsService.class);
        bind(AccountsDao.class).to(H2AccountsDao.class);
    }

    @Provides @Singleton
    DataSource provideDataSource() {
        return JdbcConnectionPool.create(DB_URL, "sa", "sa");
    }
}
