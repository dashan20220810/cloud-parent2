package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.admin.*;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.moduleauthenticator.GoogleAuthUtil;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping(value = "admin/user")
@Api(tags = "管理员管理")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private CommonBusiness commonService;

    @Value("${admin.account}")
    private String superAdmin;

    @ApiOperation(("新增管理员"))
    @PostMapping("add")
    public ResponseEntity addAdmin(AdminAddVO vo) {

        Admin currentUser = commonService.getCurrentUser();
        if (!currentUser.getUserName().equals(superAdmin)){
            return ResponseUtil.authorizationNopass();
        }
        if (Admin.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        if (Admin.checkPassword(vo.getPassword())) {
            return new ResponseEntity("密码不规范");
        }
        // 查询用户名是否存在
        Admin isExist = adminService.findByUserNameSql(vo.getUserName());
        if (Objects.nonNull(isExist)) {
            return new ResponseEntity("用户名已存在");
        }
        if (!Admin.validateIP(vo.getAllowIps())) {
            return new ResponseEntity("ip白名單不规范");
        }
        //获取当前登陆用户

        if (! GoogleAuthUtil.check_code(currentUser.getGoogleAuthKey(), vo.getAuthCode())){
            return new ResponseEntity("谷歌验证失敗");
        }
        Admin admin = createAdmin(vo, currentUser);
        admin = adminService.save(admin);
        if (Objects.isNull(admin)) {
            return ResponseUtil.fail();
        }
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT, JSON.toJSONString(admin), BackendConstants.ADMIN_MODULE);
        return ResponseUtil.success();
    }

    private Admin createAdmin(AdminAddVO vo, Admin currentUser) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(vo, admin);
        admin.setPassword(BackendServerUtil.bcrypt(vo.getPassword()));
        admin.setCreateBy(currentUser.getUserName());
        admin.setUpdateBy(currentUser.getUserName());
        return admin;
    }

    @ApiOperation(("删除管理员"))
    @PostMapping("delete")
    public ResponseEntity delete(IdVO vo) {
        if (Objects.isNull(vo.getId())) {
            return ResponseUtil.parameterNotNull();
        }
        //获取当前登陆用户
        Admin currentUser = commonService.getCurrentUser();
        if (currentUser.getId().equals(vo.getId())) {
            return new ResponseEntity("不能删除自己");
        }
        adminService.deleteById(vo.getId());
        log.info("{}删除管理员id={}", currentUser.getUserName(), vo.getId());
        return ResponseUtil.success();
    }

    @ApiOperation(("启用/禁用管理员"))
    @PostMapping("status")
    public ResponseEntity status(IdVO vo) {
        if (Objects.isNull(vo) || null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        //获取当前登陆用户
        Admin currentUser = commonService.getCurrentUser();
        if (currentUser.getId().equals(vo.getId())) {
            return new ResponseEntity("不能启用/禁用自己");
        }
        Admin admin = adminService.findAdminById(vo.getId());
        int status = admin.getStatus();
        //后端自动判断
        if (status == Constants.open) {
            status = Constants.close;
        } else {
            status = Constants.open;
        }
        adminService.statusById(status, vo.getId());
        log.info("{}修改管理员状态id={}，status={}", currentUser.getUserName(), vo.getId(), status);
        return ResponseUtil.success();
    }


    @ApiOperation(("分页"))
    @GetMapping("page")
    public ResponseEntity<Page<Admin>> page(AdminPageVO vo) {
        if (StringUtils.isNotEmpty(vo.getUserName()) && Admin.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = BackendServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), sort);
        Specification<Admin> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StringUtils.isNotBlank(vo.getUserName())) {
                predicates.add(cb.equal(root.get("userName"), vo.getUserName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<Admin> pageList = adminService.getAdminPage(spec, pageable);
        if (Objects.nonNull(pageList)) {
            List<Admin> list = pageList.getContent();
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(item -> {
                    item.setPassword(null);
                });
            }
        }
        return ResponseUtil.success(pageList);
    }

   /* @ApiOperation(("更新密码(登陆用户)"))
    @PostMapping("updatePassword")
    public ResponseEntity updatePassword(ResetPasswordVO vo) {
        if (Admin.checkPassword(vo.getNewPassword())) {
            return new ResponseEntity("新密码不规范");
        }
        //获取当前登陆用户
        Admin currentUser = commonService.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return new ResponseEntity("管理员不存在");
        }
        String bcryptPassword = currentUser.getPassword();
        boolean bcrypt = BackendServerUtil.checkBcrypt(vo.getNewPassword(), bcryptPassword);
        if (!bcrypt) {
            return new ResponseEntity("新旧密码不能一样");
        }
        //更新新密码
        adminService.updatePasswordById(BackendServerUtil.bcrypt(vo.getNewPassword()), currentUser.getId());
        log.info("{}修改管理员密码id={}", currentUser.getUserName(), currentUser.getId());
        return ResponseUtil.success();
    }*/

    @ApiOperation(("重置密码"))
    @PostMapping("updatePasswordById")
    public ResponseEntity updatePasswordById(AdminResetPasswordVO vo) {
        if (null == vo.getId()) {
            return new ResponseEntity("ID为空");
        }
        if (Admin.checkPassword(vo.getNewPassword())) {
            return new ResponseEntity("新密码不规范");
        }
        //获取当前登陆用户
        Admin currentUser = commonService.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return new ResponseEntity("管理员不存在");
        }
        //更新新密码
        adminService.updatePasswordById(BackendServerUtil.bcrypt(vo.getNewPassword()), vo.getId());
        log.info("{}修改管理员密码id={}", currentUser.getUserName(), vo.getId());
        return ResponseUtil.success();
    }

    @ApiOperation(("获取单个管理员"))
    @GetMapping("query")
    public ResponseEntity<Admin> query(IdVO vo) {
        return ResponseUtil.success(adminService.findAdminById(vo.getId()));
    }

    @ApiOperation(("编辑管理员账户"))
    @PostMapping("manageAccount")
    public ResponseEntity manageAccount(AdminUpdateVO vo) {

        Admin currentUser = commonService.getCurrentUser();
        if (!currentUser.getUserName().equals(superAdmin)){
            return ResponseUtil.authorizationNopass();
        }

        if (Admin.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        if (Admin.checkPassword(vo.getPassword())) {
            return new ResponseEntity("密码不规范");
        }
        if (!Admin.validateIP(vo.getAllowIps())) {
            return new ResponseEntity("ip白名單不规范");
        }
        //获取当前登陆用户
        if (! GoogleAuthUtil.check_code(currentUser.getGoogleAuthKey(), vo.getAuthCode())){
            return new ResponseEntity("谷歌验证失敗");
        }
        Admin user = adminService.findAdminById(vo.getId());
        Admin admin = new Admin();

        BeanUtils.copyProperties(vo, admin);
        admin.setPassword(BackendServerUtil.bcrypt(vo.getPassword()));
        admin.setGoogleAuthKey(user.getGoogleAuthKey());
        admin.setCreateBy(currentUser.getUserName());
        admin.setUpdateBy(currentUser.getUserName());
        admin.setGoogleAuthKey(currentUser.getGoogleAuthKey());

        admin = adminService.save(admin);
        if (Objects.isNull(admin)) {
            return ResponseUtil.fail();
        }
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT, JSON.toJSONString(admin), BackendConstants.ADMIN_MODULE);
        return ResponseUtil.success();
    }

}
