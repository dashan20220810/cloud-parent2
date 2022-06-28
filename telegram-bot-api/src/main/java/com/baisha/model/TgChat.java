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
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "unique_chat_bot", columnNames = {"chatId", "botId"})
})
@ApiModel(value = "TgChat对象", description = "TG群")
public class TgChat extends BaseEntity {

    @ApiModelProperty(value = "群id",required = true)
    private Long chatId;

    @ApiModelProperty(value="机器人id",required = true)
    private Long botId;

    @ApiModelProperty(value="游戏桌台id",required = true)
    private Long tableId;

    @ApiModelProperty(value="群名称",required = true)
    private String chatName;

    @ApiModelProperty(value="机器人名称",required = true)
    private String botName;

    //业务属性
    @ApiModelProperty(value="状态 0禁用 1启用",required = true)
    private Integer status;

    @ApiModelProperty(name = "单注限红最低", required = true)
    private Integer minAmount;

    @ApiModelProperty(name = "单注限红最高", required = true)
    private Integer maxAmount;

    @ApiModelProperty(name = "当局最高", required = true)
    private Integer maxShoeAmount;
}
