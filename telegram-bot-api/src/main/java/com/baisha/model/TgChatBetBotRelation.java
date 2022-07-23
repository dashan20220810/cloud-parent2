package com.baisha.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(indexes = {
    @Index(columnList = "tgChatId")
})
@ApiModel(value = "TgChatBetBotRelation对象", description = "TG群-下注机器人-关系表")
public class TgChatBetBotRelation extends BaseEntity {

    @ApiModelProperty(value = "TgChat对象-主键")
    private Long tgChatId;

    @ApiModelProperty(value = "TgBetBot对象-主键")
    private Long tgBetBotId;
}
