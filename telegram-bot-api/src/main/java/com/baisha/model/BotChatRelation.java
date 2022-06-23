package com.baisha.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity(name = "bot_chat_relation")
@ApiModel(value = "BotChatRelation对象", description = "机器人-TG群-关系表")
public class BotChatRelation extends BaseEntity {

    @ApiModelProperty("机器人id-主键")
    @Column(name = "bot_id", nullable = false)
    private Long botId;

    @ApiModelProperty("群id-主键")
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
}
