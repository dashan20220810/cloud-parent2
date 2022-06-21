package com.baisha.userserver.contrloller;

import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.userserver.model.Assets;
import com.baisha.userserver.model.User;
import com.baisha.userserver.response.UserBO;
import com.baisha.userserver.service.AssetsService;
import com.baisha.userserver.service.UserService;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.userserver.util.UserServerUtil;
import com.baisha.userserver.vo.IdVO;
import com.baisha.userserver.vo.StatusVO;
import com.baisha.userserver.vo.user.UserAddVO;
import com.baisha.userserver.vo.user.UserPageVO;
import com.baisha.userserver.vo.user.UserSearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author yihui
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AssetsService assetsService;

    @ApiOperation(("新增用户"))
    @PostMapping("addUser")
    public ResponseEntity<UserBO> addUser(UserAddVO vo) {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        if (CommonUtil.checkNull(vo.getUserName())) {
            return ResponseUtil.parameterNotNull();
        }
        if (User.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        if (StringUtils.isNoneEmpty(vo.getNickName()) && User.checkNickName(vo.getNickName())) {
            return new ResponseEntity("昵称不规范");
        }
        if (StringUtils.isNoneEmpty(vo.getPassword()) && User.checkPassword(vo.getPassword())) {
            return new ResponseEntity("密码不规范");
        }
        if (StringUtils.isNoneEmpty(vo.getIp()) && User.checkIp(vo.getIp())) {
            return new ResponseEntity("ip不规范");
        }
        // 查询用户名是否存在
        User isExist = userService.findByUserName(vo.getUserName());
        if (Objects.nonNull(isExist)) {
            return new ResponseEntity("用户名已存在");
        }
        User user = createUser(vo);
        userService.saveUser(user);
        //资产
        Assets assets = createAssets(user.getId());
        assetsService.saveAssets(assets);
        return ResponseUtil.success();
    }

    private Assets createAssets(Long userId) {
        Assets assets = new Assets();
        assets.setUserId(userId);
        assets.setBalance(BigDecimal.ZERO);
        assets.setFreezeAmount(BigDecimal.ZERO);
        return assets;
    }

    private User createUser(UserAddVO vo) {
        User user = new User();
        user.setUserName(vo.getUserName());
        user.setNickName(vo.getNickName());
        String bcryptPassword = CommonUtil.checkNull(vo.getPassword()) ? null : UserServerUtil.bcrypt(vo.getPassword());
        user.setPassword(bcryptPassword);
        user.setIp(vo.getIp());
        return user;
    }

    @ApiOperation(("用户分页"))
    @GetMapping("page")
    public ResponseEntity<Page<User>> page(UserPageVO vo) {
        if (StringUtils.isNotEmpty(vo.getUserName()) && User.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        Pageable pageable = PageRequest.of(vo.getPageNumber() - 1, vo.getPageSize());
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StringUtils.isNotBlank(vo.getUserName())) {
                predicates.add(cb.equal(root.get("userName"), vo.getUserName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<User> pageList = userService.getUserPage(spec, pageable);
        return ResponseUtil.success(pageList);
    }

    @ApiOperation(("删除用户"))
    @PostMapping("delete")
    public ResponseEntity delete(IdVO vo) {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        userService.deleteById(vo.getId());
        return ResponseUtil.success();
    }

    @ApiOperation(("启用/禁用用户"))
    @PostMapping("status")
    public ResponseEntity status(IdVO vo) {
        if (Objects.isNull(vo) || null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        User user = userService.findById(vo.getId());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }
        int status = user.getStatus();
        //后端自动判断
        if (status == Constants.open) {
            status = Constants.close;
        } else {
            status = Constants.open;
        }
        userService.statusById(status, vo.getId());
        return ResponseUtil.success();
    }

    @ApiOperation(("根据用户名称查询"))
    @GetMapping("query")
    public ResponseEntity query(UserSearchVO vo) {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        if (User.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        User user = userService.findByUserName(vo.getUserName());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }
        return ResponseUtil.success(UserBO.builder().id(user.getId()).userName(user.getUserName()).nickName(user.getNickName()).build());
    }

    @ApiOperation(("根据id称查询"))
    @GetMapping("findById")
    public ResponseEntity findById(IdVO vo) {
        if (Objects.isNull(vo.getId())) {
            return ResponseUtil.parameterNotNull();
        }
        User user = userService.findById(vo.getId());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }
        return ResponseUtil.success(UserBO.builder().id(user.getId()).userName(user.getUserName()).nickName(user.getNickName()).build());
    }

}
