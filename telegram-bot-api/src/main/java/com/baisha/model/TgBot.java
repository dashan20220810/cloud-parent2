package com.baisha.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity(name = "tg_bot")
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "unique_bot_name", columnNames = "bot_name")
})
@ApiModel(value = "TgBot对象", description = "TG机器人")
public class TgBot extends BaseEntity {

    @ApiModelProperty("机器人名称")
    @Column(name = "bot_name", nullable = false, length = 30)
    private String botName;

    @ApiModelProperty("机器人token")
    @Column(name = "bot_token", nullable = false, length = 50)
    private String botToken;

    @ApiModelProperty("机器人对应的TG群id")
    @Column(name = "chat_id", nullable = false, length = 20)
    private String chatId;

    @Column(name = "status", nullable = false)
    @ApiModelProperty("状态 1正常 2禁用")
    private Integer status;
}
