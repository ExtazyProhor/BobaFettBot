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
@Table(name = "user_statuses")
public class UserStatus implements Entity {
    @PrimaryKey
    @EntityField(name = "chat_id")
    private Long chatId;
    @EntityField(name = "status")
    private String status;
}
