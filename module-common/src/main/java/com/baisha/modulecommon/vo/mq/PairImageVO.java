package com.baisha.modulecommon.vo.mq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 14/7/22 6:43 PM
 * @Version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PairImageVO implements Serializable {

    private static final long serialVersionUID = -2072070579729803832L;

    /**
     * IP
     */
    private String dealerIp;

    /**
     * 荷官端局号
     */
    private Integer gameNo;

    /**
     * 截屏图片流
     */
    private byte[] imageContent;
}
