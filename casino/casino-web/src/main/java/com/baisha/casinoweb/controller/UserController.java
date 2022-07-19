package com.baisha.casinoweb.controller;

import java.math.BigDecimal;
import java.util.List;

import com.baisha.modulecommon.vo.mq.tgBotServer.BotGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.casinoweb.business.AssetsBusiness;
import com.baisha.casinoweb.business.UserBusiness;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.IpUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("user")
@Api(tags = { "用户控制器" })
@Slf4j
public class UserController {
	
	@Autowired
	private UserBusiness userBusiness;
	
	@Autowired
	private AssetsBusiness assetsBusiness;

	/**
	 * TG注册
	 * 
	 * @return
	 */
	@PostMapping("registerTG")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "用户名(長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"), 
		@ApiImplicitParam(name = "nickname", value = "first name + last name (長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"),
		@ApiImplicitParam(name = "groupId", value = "telegram group id", dataType = "long", required = true, paramType = "query"),
		@ApiImplicitParam(name = "inviteTgUserId", value = "邀请人 tg_user_id", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "tgGroupName", value = "tg群名称", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "tgUserName", value = "tg玩家名称", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "userType", value = "用户类型(1正式 2测试 3机器人)", dataType = "string", paramType = "query")
	})
	@ApiOperation("telegram注册")
	@NoAuthentication
	public ResponseEntity<?> registerTG(String id, String nickname, Long groupId, String inviteTgUserId,
							String tgGroupName, String tgUserName, String userType) {
		log.info("注册使用者");
		if ( CommonUtil.checkNull(id, nickname, tgGroupName) || groupId==null ) {
			log.info("注册检核失败");
			return ResponseUtil.parameterNotNull();
		}
		
		// 记录IP
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());
		if (!userBusiness.registerTG(ip, id, nickname, groupId, inviteTgUserId, tgGroupName, tgUserName, userType)) {
            return ResponseUtil.fail();
		}

		log.info("注册成功");
        return ResponseUtil.success();
	}

	/**
	 * 用户离开TG群
	 *
	 * @return
	 */
	@PostMapping("leftTG")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "用户名(長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"),
			@ApiImplicitParam(name = "groupId", value = "telegram group id", dataType = "long", required = true, paramType = "query"),
	})
	@ApiOperation("用户离开TG群")
	@NoAuthentication
	public ResponseEntity<?> leftTG(String id, Long groupId) {
		log.info("离群事件开始");
		if ( CommonUtil.checkNull(id) || groupId==null ) {
			log.info("离群检核失败");
			return ResponseUtil.parameterNotNull();
		}

		if (!userBusiness.leftTg(id, groupId)) {
			return ResponseUtil.fail();
		}

		log.info("离群事件成功");
		return ResponseUtil.success();
	}

	@PostMapping("botListByGroupId")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "groupId", value = "telegram group id", dataType = "long", required = true, paramType = "query"),
	})
	@ApiOperation("根据群ID查机器人")
	@NoAuthentication
	public ResponseEntity<List<BotGroupVO>> botListByGroupId(Long groupId) {
		log.info("根据群ID查机器人事件开始");
		if (groupId==null ) {
			log.info("根据群ID查机器人检核失败");
			return ResponseUtil.parameterNotNull();
		}

		List<BotGroupVO> botList = userBusiness.botListByGroupId(groupId);

		log.info("根据群ID查机器人事件事件成功");
		return ResponseUtil.success(botList);
	}
	

	/**
	 * 查詢余額
	 * 
	 * @return
	 */
	@PostMapping("balance")
	@ApiOperation("查詢余額")
	public ResponseEntity<BigDecimal> balance () {

		log.info("查詢余額");
		BigDecimal balance = assetsBusiness.balance();
		if ( balance==null ) {
			log.info("查詢余額失敗");
            return ResponseUtil.fail();
		}

		log.info("查詢余額成功");
        return ResponseUtil.success(balance);
	}
	
}
