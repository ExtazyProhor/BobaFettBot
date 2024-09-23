package com.prohor.personal.bobaFettBot.data;

public interface DataStorage {
    boolean containsUser(long chatId);

    void deleteUser(long chatId);
}
