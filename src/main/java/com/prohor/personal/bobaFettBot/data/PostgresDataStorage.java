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
    public <T extends Entity> boolean contains(Class<T> clazz, Object primaryKey) throws SQLException {
        Connection connection = connectionPool.getConnection();
        boolean result = PostgresCRUDMapper.contains(connection, clazz, primaryKey);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public <T extends Entity> void delete(Class<T> clazz, Object primaryKey) throws SQLException {
        Connection connection = connectionPool.getConnection();
        PostgresCRUDMapper.delete(connection, clazz, primaryKey);
        connectionPool.releaseConnection(connection);
    }

    @Override
    public <T extends Entity> T get(Class<T> clazz, Object primaryKey) throws SQLException {
        Connection connection = connectionPool.getConnection();
        T result = PostgresCRUDMapper.get(connection, clazz, primaryKey);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public <T extends Entity> T getOneByFields(T entity) throws Exception {
        Connection connection = connectionPool.getConnection();
        T result = PostgresCRUDMapper.getOneByFields(connection, entity);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public <T extends Entity> List<T> getAllByFields(T entity) throws Exception {
        Connection connection = connectionPool.getConnection();
        List<T> result = PostgresCRUDMapper.getAllByFields(connection, entity);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public <T extends Entity> List<T> getAll(Class<T> clazz) throws SQLException {
        Connection connection = connectionPool.getConnection();
        List<T> result = PostgresCRUDMapper.getAll(connection, clazz);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public <T extends Entity> int countByField(T entity) throws Exception {
        Connection connection = connectionPool.getConnection();
        int result = PostgresCRUDMapper.countByField(connection, entity);
        connectionPool.releaseConnection(connection);
        return result;
    }

    @Override
    public <T extends Entity> void create(T entity) throws SQLException {
        Connection connection = connectionPool.getConnection();
        PostgresCRUDMapper.create(connection, entity);
        connectionPool.releaseConnection(connection);
    }

    @Override
    public <T extends Entity> void update(T entity) throws SQLException {
        Connection connection = connectionPool.getConnection();
        PostgresCRUDMapper.update(connection, entity);
        connectionPool.releaseConnection(connection);
    }
}
