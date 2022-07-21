package com.baisha.userserver.contrloller;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.model.PlayMoneyChange;
import com.baisha.userserver.model.vo.balance.UserChangeBalancePageVO;
import com.baisha.userserver.model.vo.balance.UserChangePlayMoneyPageVO;
import com.baisha.userserver.service.BalanceChangeService;
import com.baisha.userserver.service.PlayMoneyChangeService;
import com.baisha.userserver.util.UserServerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("assets")
@Api(tags = "资产变动")
public class AssetsChangeController {


    @Autowired
    private BalanceChangeService balanceChangeService;

    @Autowired
    private PlayMoneyChangeService playMoneyChangeService;


    @ApiOperation(("用户余额改动分页记录"))
    @GetMapping("changeBalancePage")
    public ResponseEntity<Page<BalanceChange>> changeBalancePage(UserChangeBalancePageVO vo) {
        if (null == vo.getUserId()) {
            return ResponseUtil.parameterNotNull();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = UserServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), sort);
        Specification<BalanceChange> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(cb.equal(root.get("userId"), vo.getUserId()));
            if (null != vo.getChangeType()) {
                predicates.add(cb.equal(root.get("changeType"), vo.getChangeType()));
            }
            try {
                if (StringUtils.isNotEmpty(vo.getStartTime())) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class), DateUtil.getSimpleDateFormat().parse(vo.getStartTime().trim())));
                }
                if (StringUtils.isNotEmpty(vo.getEndTime())) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class), DateUtil.getSimpleDateFormat().parse(vo.getEndTime().trim())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<BalanceChange> pageList = balanceChangeService.getUserBalanceChangePage(spec, pageable);
        return ResponseUtil.success(pageList);
    }


    @ApiOperation(("用户打码量改动分页记录"))
    @GetMapping("changePlayMoneyPage")
    public ResponseEntity<Page<PlayMoneyChange>> changePlayMoneyPage(UserChangePlayMoneyPageVO vo) {
        if (null == vo.getUserId()) {
            return ResponseUtil.parameterNotNull();
        }
        Pageable pageable = UserServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), Sort.by(Sort.Direction.DESC, "updateTime"));
        Specification<PlayMoneyChange> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(cb.equal(root.get("userId"), vo.getUserId()));
            if (null != vo.getChangeType()) {
                predicates.add(cb.equal(root.get("changeType"), vo.getChangeType()));
            }
            try {
                if (StringUtils.isNotEmpty(vo.getStartTime())) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class), DateUtil.getSimpleDateFormat().parse(vo.getStartTime().trim())));
                }
                if (StringUtils.isNotEmpty(vo.getEndTime())) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class), DateUtil.getSimpleDateFormat().parse(vo.getEndTime().trim())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<PlayMoneyChange> pageList = playMoneyChangeService.getUserPlayMoneyChangePage(spec, pageable);
        return ResponseUtil.success(pageList);
    }


}
