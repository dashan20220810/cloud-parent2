package com.baisha.backendserver.business;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.model.bo.desk.DeskListBO;
import com.baisha.backendserver.util.constants.GameServerConstants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.util.HttpClient4Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yihui
 */
@Slf4j
@Service
public class DeskBusiness {

    @Value("${url.gameServer}")
    private String gameServerUrl;


    public List<DeskListBO> findAllDeskList() {
        String url = gameServerUrl + GameServerConstants.DESK_LIST;
        String result = HttpClient4Util.doGet(url);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        List<DeskListBO> deskList = new ArrayList<>();
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == 0) {
            JSONArray data = (JSONArray) responseEntity.getData();
            if (!CollectionUtils.isEmpty(data)) {
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    JSONObject o = data.getJSONObject(i);
                    DeskListBO bo = new DeskListBO();
                    bo.setTableId(Long.parseLong(String.valueOf(o.get("id"))));
                    bo.setDeskCode(o.getString("deskCode"));
                    bo.setName(o.getString("name"));
                    deskList.add(bo);
                }
            }
            return deskList;
        }
        return null;

    }
}
