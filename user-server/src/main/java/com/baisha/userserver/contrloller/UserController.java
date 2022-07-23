package com.baisha.userserver.contrloller;

import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.enums.UserOriginEnum;
import com.baisha.modulecommon.enums.user.UserTypeEnum;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.userserver.model.Assets;
import com.baisha.userserver.model.User;
import com.baisha.userserver.model.UserTelegramRelation;
import com.baisha.userserver.model.bo.UserBO;
import com.baisha.userserver.model.bo.UserPageBO;
import com.baisha.userserver.model.vo.IdVO;
import com.baisha.userserver.model.vo.user.*;
import com.baisha.userserver.service.AssetsService;
import com.baisha.userserver.service.UserService;
import com.baisha.userserver.service.UserTelegramRelationService;
import com.baisha.userserver.util.UserServerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.*;

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
    private UserTelegramRelationService relationService;
    @Autowired
    private AssetsService assetsService;

    @ApiOperation(("新增用户"))
    @PostMapping("save")
    public ResponseEntity saveUser(UserAddTelegramVO vo) {
        try {
            UserAddVO userAddVO = new UserAddVO();
            BeanUtils.copyProperties(vo, userAddVO);
            //先默认(Telegram注册)
            userAddVO.setOrigin(UserOriginEnum.TG_ORIGIN.getOrigin());
            return saveTelegramUser(userAddVO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseUtil.fail();
    }

    private ResponseEntity saveTelegramUser(UserAddVO vo) {
        if (StringUtils.isEmpty(vo.getTgUserId())) {
            return new ResponseEntity("TG用户ID为空");
        }
        if (StringUtils.isEmpty(vo.getTgGroupId())) {
            return new ResponseEntity("TG群ID为空");
        }
        if (StringUtils.isNoneEmpty(vo.getIp()) && User.checkIp(vo.getIp())) {
            return new ResponseEntity("ip不规范");
        }
        vo.setUserName(vo.getTgUserId());
        // 查询用户名是否存在
        User isExist = userService.findByUserName(vo.getUserName());
        if (Objects.nonNull(isExist)) {
            doUserTelegramRelation(isExist, vo);
            return ResponseUtil.success();
        }
        User user = createTelegramUser(vo);
        userService.saveUser(user);
        doUserTelegramRelation(user, vo);
        return ResponseUtil.success();
    }

    private void doUserTelegramRelation(User user, UserAddVO vo) {
        synchronized (user.getTgUserId()) {
            UserTelegramRelation relation = relationService.findByTgUserIdAndTgGroupId(user.getTgUserId(), vo.getTgGroupId());
            if (Objects.isNull(relation)) {
                relation = new UserTelegramRelation();
                relation.setUserId(user.getId());
                relation.setUserName(user.getUserName());
                relation.setTgUserId(vo.getTgUserId());
                relation.setTgGroupId(vo.getTgGroupId());
                relation.setTgGroupName(vo.getTgGroupName());
                relation.setUserType(vo.getUserType());
            } else {
                relation.setStatus(Constants.open);
            }
            relationService.save(relation);
        }
    }

    private User createTelegramUser(UserAddVO vo) {
        User user = new User();
        String bcryptPassword = CommonUtil.checkNull(vo.getPassword()) ? null : UserServerUtil.bcrypt(vo.getPassword());
        user.setPassword(bcryptPassword);
        user.setInviteCode(UserServerUtil.randomCode());

        user.setUserName(vo.getUserName());
        user.setNickName(vo.getNickName());
        user.setIp(vo.getIp());
        user.setOrigin(vo.getOrigin());
        user.setTgUserId(vo.getTgUserId());
        user.setTgUserName(vo.getTgUserName());
        user.setTgGroupId(vo.getTgGroupId());
        user.setTgGroupName(vo.getTgGroupName());
        user.setCreateBy(vo.getUserName());
        user.setUpdateBy(vo.getUserName());
        user.setUserType(vo.getUserType());
        user.setChannelCode(vo.getChannelCode());
        user.setPhone(vo.getPhone());
        //是否有tg的邀请人 先用邀请人的tgUserId查询是否存在
        if (StringUtils.isNotEmpty(vo.getInviteTgUserId())) {
            User inviteUser = userService.findByUserName(vo.getInviteTgUserId());
            if (Objects.nonNull(inviteUser)) {
                user.setInviteUserId(inviteUser.getId());
                if (StringUtils.isNotEmpty(inviteUser.getChannelCode())) {
                    user.setChannelCode(inviteUser.getChannelCode());
                }
            }
        }
        return user;
    }

    @ApiOperation(("用户分页"))
    @GetMapping("page")
    public ResponseEntity<Page<UserPageBO>> page(UserPageVO vo) {
        if (StringUtils.isNotEmpty(vo.getUserName()) && User.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = UserServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), sort);
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StringUtils.isNotEmpty(vo.getUserName())) {
                predicates.add(cb.or(
                        cb.like(root.get("userName"), "%" + vo.getUserName().trim() + "%"),
                        cb.like(root.get("tgUserName"), "%" + vo.getUserName().trim() + "%"))
                );
            }
            if (StringUtils.isNotEmpty(vo.getNickName())) {
                predicates.add(cb.like(root.get("nickName"), "%" + vo.getNickName().trim() + "%"));
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
        Page<User> pageList = userService.getUserPage(spec, pageable);
        Page<UserPageBO> page = transUserPage(pageList);
        return ResponseUtil.success(page);
    }

    private Page<UserPageBO> transUserPage(Page<User> pageList) {
        if (Objects.nonNull(pageList)) {
            List<User> userList = pageList.getContent();
            List<UserPageBO> list = new ArrayList<>(10);
            if (!CollectionUtils.isEmpty(userList)) {
                // 查询用户资产
                for (User u : userList) {
                    UserPageBO bo = new UserPageBO();
                    BeanUtils.copyProperties(u, bo);
                    bo.setPassword(null);
                    Long userId = u.getId();
                    Assets assets = assetsService.getAssetsByUserId(userId);
                    if (Objects.nonNull(assets)) {
                        bo.setBalance(assets.getBalance());
                        bo.setFreezeAmount(assets.getFreezeAmount());
                        bo.setPlayMoney(assets.getPlayMoney());
                    }
                    Long inviteUserId = bo.getInviteUserId();
                    if (null != inviteUserId) {
                        User inviteUser = userService.findById(inviteUserId);
                        if (Objects.nonNull(inviteUser)) {
                            bo.setInviteTgUserId(inviteUser.getTgUserId());
                            bo.setInviteTgUserName(inviteUser.getTgUserName());
                        }
                    }
                    list.add(bo);
                }
            }
            Page<UserPageBO> page = new PageImpl<>(list, pageList.getPageable(), pageList.getTotalElements());
            return page;
        }
        return new PageImpl<>(new ArrayList<>());
    }

    /*@ApiOperation(("删除用户"))
    @PostMapping("delete")
    public ResponseEntity delete(IdVO vo) {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        userService.deleteById(vo.getId());
        return ResponseUtil.success();
    }*/

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

    @ApiOperation(("用户查询"))
    @GetMapping("query")
    public ResponseEntity<UserBO> query(UserSearchVO vo) {
        if (StringUtils.isNotEmpty(vo.getUserName())) {
            //普通用户查询
            return findCommonUser(vo);
        } else {
            //电报用户查询
            return findTelegramUser(vo);
        }
    }

    private ResponseEntity findCommonUser(UserSearchVO vo) {
        if (User.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        User user = userService.findByUserName(vo.getUserName());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }
        return ResponseUtil.success(UserBO.builder().id(user.getId())
                .userName(user.getUserName()).nickName(user.getNickName()).status(user.getStatus()).build());
    }

    private ResponseEntity findTelegramUser(UserSearchVO vo) {
        //TG用户查询
        if (StringUtils.isEmpty(vo.getTgUserId())) {
            return new ResponseEntity("TG用户ID为空");
        }
        vo.setUserName(vo.getTgUserId());
        User user = userService.findByUserName(vo.getUserName());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }
        return ResponseUtil.success(UserBO.builder().id(user.getId()).userType(user.getUserType())
                .userName(user.getUserName()).nickName(user.getNickName()).build());
    }

    @ApiOperation(("根据id称查询"))
    @GetMapping("findById")
    public ResponseEntity<UserBO> findById(IdVO vo) {
        if (Objects.isNull(vo.getId())) {
            return ResponseUtil.parameterNotNull();
        }
        User user = userService.findById(vo.getId());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }
        return ResponseUtil.success(UserBO.builder().id(user.getId()).userType(user.getUserType())
                .userName(user.getUserName()).nickName(user.getNickName()).status(user.getStatus()).build());
    }


    @ApiOperation(("根据Tg群ID获取用户分页列表"))
    @GetMapping("findPageByTgGroupId")
    public ResponseEntity<Page<UserTelegramRelation>> findUserPageByTgGroupId(UserTgSearchPageVO vo) {
        if (StringUtils.isEmpty(vo.getTgGroupId())) {
            return new ResponseEntity("群ID必填");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = UserServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), sort);
        Specification<UserTelegramRelation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(cb.equal(root.get("tgGroupId"), vo.getTgGroupId()));
            if (StringUtils.isNotBlank(vo.getUserName())) {
                predicates.add(cb.equal(root.get("userName"), vo.getUserName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<UserTelegramRelation> pageList = relationService.getUserTelegramPage(spec, pageable);
        return ResponseUtil.success(pageList);
    }


    @ApiOperation(value = ("离群"))
    @PostMapping(value = "leaveGroup")
    public ResponseEntity leaveGroup(TgUserAndGroupIdVO vo) {
        if (CommonUtil.checkNull(vo.getTgUserId(), vo.getTgGroupId())) {
            return ResponseUtil.parameterNotNull();
        }
        relationService.leaveGroup(vo.getTgUserId(), vo.getTgGroupId());
        return ResponseUtil.success();
    }


    @ApiOperation(value = ("获取TG群的机器人用户"))
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tgGroupId", value = "群ID", required = true, dataTypeClass = String.class)
    })
    @GetMapping(value = "getBotList")
    public ResponseEntity<List<User>> getBotList(String tgGroupId) {
        if (StringUtils.isEmpty(tgGroupId)) {
            return ResponseUtil.parameterNotNull();
        }

        List<UserTelegramRelation> relations = relationService.findByTgGroupIdAndUserType(tgGroupId, UserTypeEnum.BOT.getCode());
        if (!CollectionUtils.isEmpty(relations)) {
            List<Long> userIds = relations.stream().map(item -> item.getUserId()).toList();
            List<User> list = userService.findByIdIn(userIds);
            if (!CollectionUtils.isEmpty(list)) {
                list = list.stream().map(item -> {
                    //隐藏密码
                    item.setPassword(null);
                    return item;
                }).toList();
            }

            return ResponseUtil.success(list);
        }
        return ResponseUtil.success(new ArrayList<>());
    }

    @ApiOperation(value = ("设置用户类型"))
    @PostMapping(value = "userType")
    public ResponseEntity userType(UserTypeVO vo) {
        if (null == vo.getUserType() || CommonUtil.checkNull(vo.getUserName())) {
            return ResponseUtil.parameterNotNull();
        }
        User user = userService.findByUserName(vo.getUserName());
        if (Objects.nonNull(user)) {
            userService.updateUserType(user.getId(), vo.getUserType());
        }

        return ResponseUtil.success();
    }


}
