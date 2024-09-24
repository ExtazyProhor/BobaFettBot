package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "users")
public class User extends Entity {
    @PrimaryKey
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