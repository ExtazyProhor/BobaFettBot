package com.prohor.personal.bobaFettBot.util;

import java.util.HashSet;
import java.util.Set;

public final class AdminUtils {
    private static final Set<Long> ADMINS_CHAT_IDs = new HashSet<>();

    private AdminUtils() {
    }

    public static void addAdmin(long chatId) {
        ADMINS_CHAT_IDs.add(chatId);
    }

    public static boolean isAdmin(long chatId) {
        return ADMINS_CHAT_IDs.contains(chatId);
    }
}
