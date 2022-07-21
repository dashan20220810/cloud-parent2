package com.baisha.casinoweb.business;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.BigDecimalConstants;
import com.baisha.modulecommon.vo.GameDesk;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baisha.casinoweb.model.vo.response.DeskVO;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.util.IpUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DeskBusiness {

	
    @Value("${project.server-url.game-server-domain}")
    private String gameServerDomain;
	@Autowired
	private RedissonClient redisUtil;

    /**
     * game server查桌台号
     * @return
     */
    public DeskVO queryDeskByIp() {
		String localIp = IpUtil.getIp(CasinoWebUtil.getRequest());
		return queryDeskByIp(localIp);
    }
    
    /**
     * game server查桌台号
     * @return
     */
    public DeskVO queryDeskByIp(String localIp) {

    	log.info("查桌台号");
		log.info("ip: {}", localIp);

		try {
			return queryDesk(RequestPathEnum.DESK_QUERY_BY_LOCAL_IP.getApiName()
					, "?localIp=" +URLEncoder.encode(String.valueOf(localIp), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
    		log.error("查桌台号 失败", e);
            return null;
		}
    }
    

    /**
     * game server查桌台号
     * @return
     */
    public DeskVO queryDeskByDeskCode( String deskCode ) {

    	log.info("查桌台号");
		log.info("deskCode: {}", deskCode);

		try {
			return queryDesk(RequestPathEnum.DESK_QUERY_BY_DESK_CODE.getApiName()
					, "?deskCode=" +URLEncoder.encode(String.valueOf(deskCode), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
    		log.error("查桌台号 失败", e);
            return null;
		}
    }

    /**
     * game server查桌台号
     * @return
     */
	public DeskVO queryDeskById ( Long tableId ) {

    	log.info("查桌台号");
		log.info("tableId: {}", tableId);

		try {
			return queryDesk(RequestPathEnum.DESK_QUERY_BY_ID.getApiName()
					, "?tableId=" +URLEncoder.encode(String.valueOf(tableId), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
    		log.error("查桌台号 失败", e);
            return null;
		}
    }
	
	
	private DeskVO queryDesk ( String api, String urlParam )  {
		String result = HttpClient4Util.doGet(gameServerDomain + api +urlParam);

        if (CommonUtil.checkNull(result)) {
    		log.warn("查桌台号 失败");
            return null;
        }
        
		JSONObject json = JSONObject.parseObject(result);
		Integer code = json.getInteger("code");

		if ( code!=0 ) {
    		log.warn("查桌台号 失败, {}", json.toString());
            return null;
		}

    	log.info("查桌台号 成功");

    	return JSONObject.parseObject(json.getString("data"), new TypeReference<DeskVO>(){});
	}

}
