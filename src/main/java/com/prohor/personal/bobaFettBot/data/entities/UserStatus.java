package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.*;
import lombok.*;

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
