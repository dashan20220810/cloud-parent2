package com.baisha.casinoweb.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@ApiModel(value = "TgImage对象", description = "telegram image")
public class TgImage extends BaseEntity{

	private static final long serialVersionUID = -6921945481311608589L;

	@ApiModelProperty("name")
    @Column(name="name")
	private String name;

	@ApiModelProperty("tg_image")
    @Column(name="tg_image")
	private String tgImage;
}
