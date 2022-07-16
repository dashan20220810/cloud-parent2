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
@ApiModel(value = "后台-注单重开牌结果变化日志", description = "注单重开牌结果变化日志")
public class BetResultChange extends BaseEntity {

    @ApiModelProperty("桌台ID")
    @Column(columnDefinition = "bigint(11) COMMENT '桌台ID'")
    private Long tableId;

    @ApiModelProperty("游戏局号")
    @Column(nullable = false, columnDefinition = "VARCHAR(50) COMMENT '游戏局号'")
    private String noActive;

    @ApiModelProperty("开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,SS2.SS3幸运六")
    @Column(columnDefinition = "VARCHAR(20) COMMENT '开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,SS2.SS3幸运六'")
    private String awardOption;

    @ApiModelProperty("重开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS2.SS3幸运六")
    @Column(columnDefinition = "VARCHAR(20) COMMENT '重开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,SS2.SS3幸运六'")
    private String finalAwardOption;


}
