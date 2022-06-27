package com.baisha.backendserver.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author yihui
 */
//@Slf4j
@Data
//@Entity
//@org.hibernate.annotations.Table(appliesTo = "tg_group_bound", comment = "电报群限红")
public class TgGroupBound extends BaseEntity {

    @Column(unique = true, columnDefinition = "varchar(64) comment 'TG群ID'")
    private String tgGroupId;

    //限红：单注20-15000  当局最高50000（美金)
    @Column(columnDefinition = "int(11) comment '单注最低'")
    private Integer minAmount = 20;

    @Column(columnDefinition = "int(11) comment '单注最高'")
    private Integer maxAmount = 15000;

    @Column(columnDefinition = "int(11) comment '当局最高'")
    private Integer maxShoeAmount = 50000;


}
