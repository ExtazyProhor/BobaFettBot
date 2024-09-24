package com.prohor.personal.bobaFettBot.data;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;

import java.sql.*;
import java.util.*;

public class PostgresDataStorage implements DataStorage {
    private final ConnectionPool connectionPool;

    public PostgresDataStorage(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public boolean contains(Class<? extends Entity> clazz, Object primaryKey) throws SQLException {
        Connection connection = connectionPool.getConnection();
        boolean result = PostgresMapper.contains(connection, clazz, primaryKey);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public void delete(Class<? extends Entity> clazz, Object primaryKey) throws SQLException {
        Connection connection = connectionPool.getConnection();
        PostgresMapper.delete(connection, clazz, primaryKey);
        connectionPool.releaseConnection(connection);
    }

    @Override
    public <T extends Entity> T get(Class<T> clazz, Object primaryKey) throws SQLException {
        Connection connection = connectionPool.getConnection();
        T result = PostgresMapper.get(connection, clazz, primaryKey);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public <T extends Entity> List<T> getAll(Class<T> clazz) throws SQLException {
        Connection connection = connectionPool.getConnection();
        List<T> result = PostgresMapper.getAll(connection, clazz);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public void create(Entity entity) throws SQLException {
        Connection connection = connectionPool.getConnection();
        PostgresMapper.create(connection, entity);
        connectionPool.releaseConnection(connection);
    }

    @Override
    public void update(Entity entity) throws SQLException {
        Connection connection = connectionPool.getConnection();
        PostgresMapper.update(connection, entity);
        connectionPool.releaseConnection(connection);
    }
}
