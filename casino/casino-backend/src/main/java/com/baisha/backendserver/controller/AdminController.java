package com.baisha.backendserver.controller;

import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.vo.IdVO;
import com.baisha.backendserver.vo.StatusVO;
import com.baisha.backendserver.vo.admin.AdminAddVO;
import com.baisha.backendserver.vo.admin.AdminPageVO;
import com.baisha.backendserver.vo.admin.AdminUpdatePasswordVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation(("新增管理员"))
    @PostMapping("add")
    public ResponseEntity addAdmin(AdminAddVO vo) {
        if (Admin.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        if (Admin.checkNickName(vo.getNickName())) {
            return new ResponseEntity("昵称不规范");
        }
        if (Admin.checkPassword(vo.getPassword())) {
            return new ResponseEntity("密码不规范");
        }
        if (Admin.checkPhone(vo.getPhone())) {
            return new ResponseEntity("手机号不规范");
        }
        // 查询用户名是否存在
        Admin isExist = adminService.findByUserNameSql(vo.getUserName());
        if (Objects.nonNull(isExist)) {
            return new ResponseEntity("用户名已存在");
        }
        Admin admin = createAdmin(vo);
        admin = adminService.save(admin);
        if (Objects.isNull(admin)) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.success();
    }

    private Admin createAdmin(AdminAddVO vo) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(vo, admin);
        admin.setPassword(BackendServerUtil.bcrypt(vo.getPassword()));
        return admin;
    }

    @ApiOperation(("删除管理员"))
    @PostMapping("delete")
    public ResponseEntity delete(IdVO vo) {
        if (Objects.isNull(vo.getId())) {
            return ResponseUtil.parameterNotNull();
        }
        adminService.doDelete(vo.getId());
        return ResponseUtil.success();
    }

    @ApiOperation(("启用/禁用管理员"))
    @PostMapping("status")
    public ResponseEntity status(StatusVO vo) {
        if (Objects.isNull(vo) || null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        if (BackendServerUtil.checkStatus(vo.getStatus())) {
            return new ResponseEntity("状态不规范");
        }
        adminService.doStatus(vo.getStatus(), vo.getId());
        return ResponseUtil.success();
    }


    @ApiOperation(("分页"))
    @GetMapping("page")
    public ResponseEntity<Page<Admin>> page(AdminPageVO vo) {
        if (StringUtils.isNotEmpty(vo.getUserName()) && Admin.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        Page<Admin> pageList = adminService.getAdminPage(vo);
        return ResponseUtil.success(pageList);
    }


    @ApiOperation(("更新密码"))
    @GetMapping("updatePassword")
    public ResponseEntity updatePassword(AdminUpdatePasswordVO vo) {
        if (Admin.checkPassword(vo.getOldPassword())) {
            return new ResponseEntity("旧密码不规范");
        }
        if (Admin.checkPassword(vo.getNewPassword())) {
            return new ResponseEntity("新密码不规范");
        }
        if (vo.getOldPassword().equals(vo.getNewPassword())) {
            return new ResponseEntity("新旧密码不能一样");
        }
        //获取管理员用户 应该获取当前后台登录用户 目前先用ID查询
        Long authId = BackendServerUtil.getCurrentUserId();
        Admin admin = adminService.findAdminById(authId);
        if (Objects.isNull(admin)) {
            return new ResponseEntity("管理员不存在");
        }
        String bcryptPassword = admin.getPassword();
        boolean bcrypt = BackendServerUtil.checkBcrypt(vo.getOldPassword(), bcryptPassword);
        if (!bcrypt) {
            return new ResponseEntity("旧密码错误");
        }
        //更新新密码
        adminService.doUpdatePassword(BackendServerUtil.bcrypt(vo.getNewPassword()), authId);
        return ResponseUtil.success();
    }

    @ApiOperation(("获取单个管理员"))
    @GetMapping("query")
    public ResponseEntity query(IdVO vo) {
        return ResponseUtil.success(adminService.findAdminById(vo.getId()));
    }

}
