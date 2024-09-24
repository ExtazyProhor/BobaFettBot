package com.prohor.personal.bobaFettBot.data;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;

import java.util.List;


public interface DataStorage {
    boolean contains(Class<? extends Entity> clazz, Object primaryKey) throws Exception;

    void delete(Class<? extends Entity> clazz, Object primaryKey) throws Exception;

    <T extends Entity> T get(Class<T> clazz, Object primaryKey) throws Exception;

    <T extends Entity> List<T> getAll(Class<T> clazz) throws Exception;

    void create(Entity entity) throws Exception;

    void update(Entity entity) throws Exception;
}
