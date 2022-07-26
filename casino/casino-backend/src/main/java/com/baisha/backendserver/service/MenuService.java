package com.baisha.backendserver.service;

import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.Menu;
import com.baisha.backendserver.model.bo.Menu.MenuBO;
import com.baisha.backendserver.repository.MenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = "backend::Menu")
@Transactional(rollbackFor = Exception.class)
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public Menu addMenu(Menu menu){
        if (0 == menu.getParentId().longValue()){
            menu.setLevel(1);
            menu.setParentId(0L);
        } else {
            Menu parentMenu = menuRepository.findById(menu.getParentId()).orElse(null);
            if (null == parentMenu) {
                log.error("未查询到对应的父节点");
                return null;
            }
            menu.setLevel(parentMenu.getLevel().intValue() + 1);
            if (StringUtils.isNotEmpty(parentMenu.getPath())) {
                menu.setPath(parentMenu.getPath() + "," + parentMenu.getId());
            } else {
                menu.setPath(parentMenu.getId().toString());
            }
        }
        menuRepository.save(menu);
        return menuRepository.getById(menu.getId());
    }

    public List<Menu> queryMenuTree() {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC,"level"));
        orders.add(new Sort.Order(Sort.Direction.ASC,"id"));
        List<Menu> menuList = menuRepository.findAll(Sort.by(orders));
        return menuList;
    }

}
