package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "chat_owners")
public class ChatOwner implements Entity {
    @PrimaryKey
    @EntityField(name = "chat_id")
    private Long chatId;
    @EntityField(name = "owner_id")
    private Long ownerId;
}
