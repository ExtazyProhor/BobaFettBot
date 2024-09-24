package com.prohor.personal.bobaFettBot.data.entities;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class User {
    private long chatId;
    private String chatType;
    private String chatName;
    private String userLink;

    public User createSingleUser(long chatId, String name, String link) {
        return new User(chatId, "private", name, link);
    }

    public User createChatUser(long chatId, ChatType chatType, String chatName) {
        return new User(chatId, chatType.type, chatName, null);
    }

    public enum ChatType {
        GROUP("group"),
        SUPERGROUP("supergroup"),
        CHANNEL("channel");

        ChatType(String type) {
            this.type = type;
        }

        private final String type;
    }
}