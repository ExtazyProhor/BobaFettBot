package com.prohor.personal.bobaFettBot.data.entities;


import com.prohor.personal.bobaFettBot.data.mapping.Entity;
import com.prohor.personal.bobaFettBot.data.mapping.EntityField;
import com.prohor.personal.bobaFettBot.data.mapping.PrimaryKey;
import com.prohor.personal.bobaFettBot.data.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "users")
public class User implements Entity {
    @PrimaryKey
    @EntityField(name = "chat_id")
    private Long chatId;
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
