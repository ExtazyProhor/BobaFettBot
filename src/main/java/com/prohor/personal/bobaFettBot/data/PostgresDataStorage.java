package com.prohor.personal.bobaFettBot.data;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PostgresDataStorage implements DataStorage {
    private static final Logger log = LoggerFactory.getLogger(PostgresDataStorage.class);
    private final HikariDataSource dataSource;

    public PostgresDataStorage(String url, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setIdleTimeout(30_000);
        config.setMaxLifetime(600_000);
        config.setConnectionTimeout(5_000);
        dataSource = new HikariDataSource(config);
    }

    private Connection getConnection() throws SQLException {
        log.trace("getting connection");
        return dataSource.getConnection();
    }

    @Override
    public <T extends Entity> boolean contains(Class<T> clazz, Object primaryKey) throws SQLException {
        try (Connection connection = getConnection()) {
            return PostgresCRUDMapper.contains(connection, clazz, primaryKey);
        }
    }

    @Override
    public <T extends Entity> boolean containsByFields(T entity) throws Exception {
        try (Connection connection = getConnection()) {
            return PostgresCRUDMapper.containsByFields(connection, entity);
        }
    }

    @Override
    public <T extends Entity> void delete(Class<T> clazz, Object primaryKey) throws SQLException {
        try (Connection connection = getConnection()) {
            PostgresCRUDMapper.delete(connection, clazz, primaryKey);
        }
    }

    @Override
    public <T extends Entity> T get(Class<T> clazz, Object primaryKey) throws SQLException {
        try (Connection connection = getConnection()) {
            return PostgresCRUDMapper.get(connection, clazz, primaryKey);
        }
    }

    @Override
    public <T extends Entity> T getOneByFields(T entity) throws Exception {
        try (Connection connection = getConnection()) {
            return PostgresCRUDMapper.getOneByFields(connection, entity);
        }
    }

    @Override
    public <T extends Entity> List<T> getAllByFields(T entity) throws Exception {
        try (Connection connection = getConnection()) {
            return PostgresCRUDMapper.getAllByFields(connection, entity);
        }
    }

    @Override
    public <T extends Entity> List<T> getAll(Class<T> clazz) throws SQLException {
        try (Connection connection = getConnection()) {
            return PostgresCRUDMapper.getAll(connection, clazz);
        }
    }

    @Override
    public <T extends Entity> int countByFields(T entity) throws Exception {
        try (Connection connection = getConnection()) {
            return PostgresCRUDMapper.countByFields(connection, entity);
        }
    }

    @Override
    public <T extends Entity> void create(T entity) throws SQLException {
        try (Connection connection = getConnection()) {
            PostgresCRUDMapper.create(connection, entity);
        }
    }

    @Override
    public <T extends Entity> void update(T entity) throws SQLException {
        try (Connection connection = getConnection()) {
            PostgresCRUDMapper.update(connection, entity);
        }
    }
}
