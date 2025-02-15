package com.prohor.personal.bobaFettBot.data;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;

import java.util.List;

public interface DataStorage {
    <T extends Entity> boolean contains(Class<T> clazz, Object primaryKey) throws Exception;

    <T extends Entity> boolean containsByFields(T entity) throws Exception;

    <T extends Entity> void delete(Class<T> clazz, Object primaryKey) throws Exception;

    <T extends Entity> T get(Class<T> clazz, Object primaryKey) throws Exception;

    <T extends Entity> T getOneByFields(T entity) throws Exception;

    <T extends Entity> List<T> getAllByFields(T entity) throws Exception;

    <T extends Entity> List<T> getAll(Class<T> clazz) throws Exception;

    <T extends Entity> int countByFields(T entity) throws Exception;

    <T extends Entity> void create(T entity) throws Exception;

    <T extends Entity> void update(T entity) throws Exception;
}
