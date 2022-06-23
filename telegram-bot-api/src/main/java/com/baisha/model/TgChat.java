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
@Entity(name = "tg_chat")
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "unique_chat_id", columnNames = "chat_id")
})
@ApiModel(value = "TgChat对象", description = "TG群")
public class TgChat extends BaseEntity {

    @ApiModelProperty("群id")
    @Column(name = "chat_id", nullable = false, length = 20)
    private String chatId;

    @ApiModelProperty("群名称")
    @Column(name = "chat_name", nullable = false, length = 100)
    private String chatName;
}
