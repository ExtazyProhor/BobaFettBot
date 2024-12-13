package com.prohor.personal.bobaFettBot.data;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;

import java.sql.*;
import java.util.*;

public class PostgresDataStorage implements DataStorage {
    private final String url;
    private final String user;
    private final String password;

    public PostgresDataStorage(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
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
