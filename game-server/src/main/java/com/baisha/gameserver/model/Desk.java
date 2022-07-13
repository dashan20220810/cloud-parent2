package com.baisha.gameserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sys
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@org.hibernate.annotations.Table(appliesTo = "desk", comment = "电报桌台")
@ApiModel(value = "电报桌台")
public class Desk extends BaseEntity {

    private static final long serialVersionUID = -5094965147919443314L;

	@ApiModelProperty(value = "桌台编码(1-10位 例如G26)")
    @Column(unique = true, columnDefinition = "varchar(10) comment '桌台编码(例如G26)'")
    private String deskCode;

	@ApiModelProperty(value = "桌台名称(1-30位 )")
    @Column(unique = true, columnDefinition = "varchar(30) comment '桌台名称'")
    private String name;

    @ApiModelProperty(value = "内网IP(1-20位)")
    @Column(unique = true, columnDefinition = "varchar(20) comment '内网IP'")
    private String localIp;

    @ApiModelProperty(value = "游戏视频地址")
    @Column(columnDefinition = "varchar(100) comment '游戏视频地址'")
    private String videoAddress;

    @ApiModelProperty(value = "游戏视频地址(近景)")
    @Column(columnDefinition = "varchar(100) comment '游戏视频地址(近景)'")
    private String nearVideoAddress;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    @Column(columnDefinition = "tinyint(2) comment '状态 1 正常 ，0禁用'")
    private Integer status = 1;

    @ApiModelProperty(value = "游戏编码")
    @Column(columnDefinition = "varchar(10) comment '游戏编码'")
    private String gameCode = "BACC";

    /**
     * 检核下注请求
     * @param bet
     * @return
     */
    public static boolean checkRequest ( Desk desk ) {
    	
    	if ( StringUtils.isBlank(desk.getDeskCode()) ) {
    		log.warn(" deskCode required!! ");
    		return false;
    	}
    	
    	if ( StringUtils.isBlank(desk.getName()) ) {
    		log.warn(" name required!! ");
    		return false;
    	}

    	if ( StringUtils.isBlank(desk.getLocalIp()) ) {
    		log.warn(" localIp required!! ");
    		return false;
    	}

    	if ( StringUtils.isBlank(desk.getGameCode()) ) {
    		log.warn(" gameCode required!! ");
    		return false;
    	}

    	if ( desk.getStatus()==null ) {
    		log.warn(" status required!! ");
    		return false;
    	}
    	
    	return true;
    }

    /**
     * 检核桌台更新请求
     * @param bet
     * @return
     */
    public static boolean checkUpdateRequest ( Desk desk ) {
    	
    	if ( StringUtils.isBlank(desk.getName()) ) {
    		log.warn(" name required!! ");
    		return false;
    	}

    	if ( StringUtils.isBlank(desk.getLocalIp()) ) {
    		log.warn(" localIp required!! ");
    		return false;
    	}

    	if ( StringUtils.isBlank(desk.getGameCode()) ) {
    		log.warn(" gameCode required!! ");
    		return false;
    	}

    	if ( desk.getStatus()==null ) {
    		log.warn(" status required!! ");
    		return false;
    	}
    	
    	return true;
    }

}
