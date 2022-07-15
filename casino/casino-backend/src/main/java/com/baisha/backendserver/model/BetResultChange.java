package com.baisha.backendserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


@Data
@Entity
@Slf4j
@org.hibernate.annotations.Table(appliesTo = "bet_result_change", comment = "注单修改结果日志")
@Table(name = "BetResultChange", indexes = {@Index(columnList = "noActive"), @Index(columnList = "tableId")})
@ApiModel(value = "后台-注单结果日志", description = "重开牌结果")
public class BetResultChange extends BaseEntity {

    @ApiModelProperty("table_id")
    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @ApiModelProperty("游戏局号")
    @Column(name = "no_active", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '游戏局号'")
    private String noActive;

    @ApiModelProperty("开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,SS幸运六")
    @Column(name = "award_option", columnDefinition = "VARCHAR(20) COMMENT '开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六'")
    private String awardOption;

    @ApiModelProperty("重开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六")
    @Column(name = "final_award_option"
            , columnDefinition = "VARCHAR(20) COMMENT '重开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六'")
    private String finalAwardOption;


}
