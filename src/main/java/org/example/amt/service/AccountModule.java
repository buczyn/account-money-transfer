package org.example.amt.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.DataSource;
import java.sql.*;

@Slf4j
public class AccountModule extends AbstractModule {
    private static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String INIT_DB_URL = DB_URL + ";INIT=runscript from 'classpath:create.sql'\\;runscript from 'classpath:init.sql'";

    @Override
    protected void configure() {
        bind(AccountService.class).to(DefaultAccountService.class);
        bind(AccountSettingsService.class).to(HardcodedAccountSettingsService.class);
        bind(AccountsDao.class).to(H2AccountsDao.class);
    }

    @Provides @Singleton
    DataSource provideDataSource() throws SQLException {

        try (@SuppressWarnings("unused") Connection connection = DriverManager.getConnection(INIT_DB_URL)) {
            // do nothing, just to initialize database with schema
        }

        return JdbcConnectionPool.create(DB_URL, "", "");
    }
}
