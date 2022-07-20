package com.baisha.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
    @UniqueConstraint(name = "unique_bet_bot_id", columnNames = "betBotId")
})
@ApiModel(value = "TgBetBot对象", description = "TG下注机器人")
public class TgBetBot extends BaseEntity {

    @ApiModelProperty(value = "机器人id")
    private String betBotId;

    @ApiModelProperty(value = "机器人手机号")
    private String betBotPhone;

    @ApiModelProperty(value = "投注开始时间")
    private String betStartTime;

    @ApiModelProperty(value = "投注结束时间")
    private String betEndTime;

    @ApiModelProperty(value = "投注频率")
    private Integer betFrequency;

    @ApiModelProperty(value = "投注内容")
    private String betContents;

    @ApiModelProperty(value = "投注金额-最小倍数")
    private Integer minMultiple;

    @ApiModelProperty(value = "投注金额-最大倍数")
    private Integer maxMultiple;

    @ApiModelProperty(value = "状态 0禁用 1启用")
    private Integer status;
}
