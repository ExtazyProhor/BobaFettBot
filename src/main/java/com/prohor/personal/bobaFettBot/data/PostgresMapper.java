package com.prohor.personal.bobaFettBot.data;

import com.prohor.personal.bobaFettBot.data.mapping.*;

import java.lang.reflect.*;
import java.sql.*;
import java.time.*;
import java.util.*;

public class PostgresMapper {
    public static boolean contains(Connection connection, Class<? extends Entity> clazz, Object primaryKey)
            throws SQLException {

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT COUNT(*) FROM " + getTableName(clazz) + " WHERE " +
                    getPrimaryKeyColumnName(clazz) + " = " + wrapObject(primaryKey) + ";";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);

            return resultSet.next() && resultSet.getInt(1) == 1;
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
    }

    public static void delete(Connection connection, Class<? extends Entity> clazz, Object primaryKey)
            throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = "DELETE FROM " + getTableName(clazz) + " WHERE " +
                    getPrimaryKeyColumnName(clazz) + " = " + wrapObject(primaryKey) + ";";
            statement.execute(sql);
        }
    }


    public static <T extends Entity> T get(Connection connection, Class<T> clazz, Object primaryKey)
            throws SQLException {

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "SELECT * FROM " + getTableName(clazz) + " WHERE " +
                    getPrimaryKeyColumnName(clazz) + " = " + wrapObject(primaryKey) + ";";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            return createEntityFromResultSet(clazz, resultSet);
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
    }

    public static <T extends Entity> List<T> getAll(Connection connection, Class<T> clazz)
            throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            List<T> entities = new ArrayList<>();
            String sqlQuery = "SELECT * FROM " + getTableName(clazz) + ";";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                entities.add(createEntityFromResultSet(clazz, resultSet));
            }
            return entities;
        } finally {
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }
    }

    public static void create(Connection connection, Entity entity) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            Map<String, String> map = new HashMap<>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                EntityField entityField = field.getAnnotation(EntityField.class);
                if (entityField == null)
                    continue;
                Object value = null;
                try {
                    value = field.get(entity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value == null)
                    continue;
                map.put(entityField.n(), wrapObject(value));
            }
            if (map.size() == 0)
                throw new MappingException("entity " + entity + " has no writable fields");

            StringBuilder values = new StringBuilder();
            StringBuilder command = new StringBuilder();
            command.append("INSERT INTO ").append(getTableName(entity.getClass())).append(" (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                command.append(entry.getKey()).append(", ");
                values.append(entry.getValue()).append(", ");
            }
            values.setLength(values.length() - 2);
            command.setLength(command.length() - 2);
            command.append(") VALUES (").append(values).append(");");

            statement.execute(command.toString());
        }
    }

    public static void update(Connection connection, Entity entity) throws SQLException {
        Object primaryKey = getPrimaryKey(entity);
        if (!contains(connection, entity.getClass(), primaryKey)) {
            create(connection, entity);
            return;
        }

        try (Statement statement = connection.createStatement()) {
            Entity existing = get(connection, entity.getClass(), primaryKey);
            Map<String, String> map = new HashMap<>();

            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                EntityField entityField = field.getAnnotation(EntityField.class);
                if (entityField == null)
                    continue;

                try {
                    Object entityFieldValue = field.get(entity);
                    Object existingFieldValue = field.get(existing);
                    if (existingFieldValue != null && !existingFieldValue.equals(entityFieldValue))
                        map.put(entityField.n(), wrapObject(entityFieldValue));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (map.size() == 0)
                throw new MappingException("entities are identical");

            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE ").append(getTableName(entity.getClass())).append(" SET ");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sql.append(entry.getKey()).append(" = ").append(entry.getValue()).append(", ");
            }
            sql.setLength(sql.length() - 2);
            sql.append(";");

            statement.execute(sql.toString());
        }
    }

    private static String getPrimaryKeyColumnName(Class<? extends Entity> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey == null)
                continue;
            EntityField entityField = field.getAnnotation(EntityField.class);
            if (entityField == null)
                throw new MappingException("primary key field must be annotated with " + EntityField.class);
            return Objects.requireNonNull(entityField.n(), "field " + field.getName() + " name is null in " + clazz);
        }
        throw new MappingException("class " + EntityField.class + " does not contain a primary key");
    }

    private static Object getPrimaryKey(Entity entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey == null)
                continue;
            EntityField entityField = field.getAnnotation(EntityField.class);
            if (entityField == null)
                throw new MappingException(
                        "primary key field must be annotated with " + EntityField.class + " in " + entity.getClass());
            try {
                return Objects.requireNonNull(field.get(entity), "primary key of " + entity + " is null");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new MappingException("entity " + entity + " does not contain a primary key");
    }

    private static String getTableName(Class<? extends Entity> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null)
            throw new MappingException("class " + clazz + " is not a table");
        return Objects.requireNonNull(table.name(), "table name is null in " + clazz);
    }

    private static String wrapObject(Object object) {
        if (object instanceof String || object instanceof LocalDate || object instanceof LocalTime) {
            return "'" + object + "'";
        } else if (object instanceof Number) {
            return object.toString();
        } else {
            throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
        }
    }

    private static <T extends Entity> T createEntityFromResultSet(Class<T> clazz, ResultSet resultSet)
            throws SQLException {

        T entity = null;
        try {
            entity = clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new MappingException("class " + clazz + " does not contains no args constructor");
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        if (entity == null)
            throw new MappingException("failed to initialize entity of class " + clazz);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            EntityField entityField = field.getAnnotation(EntityField.class);
            if (entityField == null)
                continue;

            try {
                field.set(entity, resultSet.getObject(entityField.n(), field.getType()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }
}
