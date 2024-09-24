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
    @EntityField(name = "chat_id")
    private long chatId;
    @EntityField(name = "chat_type")
    private String chatType;
    @EntityField(name = "chat_name")
    private String chatName;
    @EntityField(name = "user_link")
    private String userLink;

    public User(long chatId, String chatType, String chatName) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.chatName = chatName;
    }
}