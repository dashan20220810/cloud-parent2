package com.baisha.userserver.contrloller;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.model.vo.balance.UserChangeBalancePageVO;
import com.baisha.userserver.service.BalanceChangeService;
import com.baisha.userserver.service.PlayMoneyChangeService;
import com.baisha.userserver.util.UserServerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
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


    @ApiOperation(("用户余额记录"))
    @GetMapping("changeBalancePage")
    public ResponseEntity<Page<BalanceChange>> changeBalancePage(UserChangeBalancePageVO vo) {
        if (null == vo.getUserId()) {
            return ResponseUtil.parameterNotNull();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = UserServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), sort);
        Specification<BalanceChange> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(cb.equal(root.get("userId"), vo.getUserId()));
            if (null != vo.getChangeType()) {
                predicates.add(cb.equal(root.get("changeType"), vo.getChangeType()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<BalanceChange> pageList = balanceChangeService.getUserBalanceChangePage(spec, pageable);
        return ResponseUtil.success(pageList);
    }


}
