package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class User extends Entity {
    private long chatId;
    private String chatType;
    private String chatName;
    private String userLink;

    public User(long chatId, String chatType, String chatName) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.chatName = chatName;
    }
}