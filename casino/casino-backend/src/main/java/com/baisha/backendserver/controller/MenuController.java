package com.baisha.backendserver.controller;

import com.baisha.backendserver.model.Menu;
import com.baisha.backendserver.model.bo.Menu.MenuBO;
import com.baisha.backendserver.model.vo.Menu.MenuVO;
import com.baisha.backendserver.service.MenuService;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "admin/menu")
@Api(tags = "菜單功能管理")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation(("新增功能清單"))
    @PostMapping(value = "/addMenu")
    public ResponseEntity addMenu(MenuVO menuVO){
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuVO, menu);
        menu = menuService.addMenu(menu);
        if (Objects.isNull(menu)) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.success();
    }

    @ApiOperation(("查詢所有的菜單功能清單"))
    @PostMapping(value = "/queryMenuTree")
    public ResponseEntity queryTreeMenu(){
        List<Menu> menuList = menuService.queryMenuTree();
        List<MenuBO> resultList = transferMenuVo(menuList, 0L);
        return ResponseUtil.success(resultList);
    }

    private List<MenuBO> transferMenuVo(List<Menu> menuList, Long parentId) {
        List<MenuBO> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(menuList)){
            for (Menu source : menuList) {
                if(parentId.longValue() == source.getParentId().longValue()){
                    MenuBO menuBO = new MenuBO();
                    BeanUtils.copyProperties(source, menuBO);
                    //递归查询子菜单，并封装信息
                    List<MenuBO> childList = transferMenuVo(menuList, source.getId());
                    if(!CollectionUtils.isEmpty(childList)){
                        menuBO.setChildMenu(childList);
                    }
                    resultList.add(menuBO);
                }
            }
        }
        return resultList;
    }
}
