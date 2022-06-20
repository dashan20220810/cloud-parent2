//package com.baisha.controller;
//
//import com.baisha.model.User;
//import com.baisha.modulecommon.reponse.ResponseEntity;
//import com.baisha.modulecommon.reponse.ResponseUtil;
//import com.baisha.service.UserService;
//import com.baisha.util.TelegramBotUtil;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("user")
//@Api("用户管理")
//public class UserController {
//
//    @Autowired
//    UserService userService;
//
//    @PostMapping("register")
//    @ApiOperation(("帐密注册"))
//    @ApiImplicitParams({
//            @ApiImplicitParam(name="name",value="用户名",dataType="string", paramType = "query",example="dashan"),
//            @ApiImplicitParam(name="password",value="密码",dataType="string", paramType = "query",example="123456"),
//    })
//    public ResponseEntity<User> register(String name, String password) {
//        User user=new User();
//        user.setName(name);
//        user.setPassword(password);
//        userService.save(user);
//        return ResponseUtil.success(user);
//    }
//
//    @GetMapping("findById")
//    @ApiOperation(("根据ID查询用户"))
//    @ApiImplicitParams({
//            @ApiImplicitParam(name="id",value="用户名",dataType="numeric", paramType = "query",example="1"),
//    })
//    public  ResponseEntity<User> findById(Long id){
//        User one = userService.findOne(id);
//        return ResponseUtil.success(one);
//
//    }
//
//    @GetMapping("addNickname")
//    @ApiOperation(("添加昵称"))
//    @ApiImplicitParams({
//            @ApiImplicitParam(name="nickname",value="昵称",dataType="String", paramType = "query",example="1"),
//    })
//    public ResponseEntity<User> addNickname(String nickname){
//        Long id = TelegramBotUtil.getCurrentUserId();
//        userService.updateNicknameById(nickname,id);
//        return ResponseUtil.success();
//
//    }
//}
