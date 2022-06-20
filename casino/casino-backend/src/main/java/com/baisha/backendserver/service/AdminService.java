package com.baisha.backendserver.service;

import com.baisha.backendserver.constants.BackendServerConstants;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.repository.AdminRepository;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.vo.admin.AdminPageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @author yihui
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "backend::admin")
@Transactional(rollbackFor = Exception.class)
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;


    public Admin findByUserNameSql(String userName) {
        return adminRepository.findByUserName(userName);
    }

    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        adminRepository.deleteById(id);
    }

    @CachePut(key = "#id")
    public Admin statusById(Integer status, Long id) {
        adminRepository.updateAdminStatusById(status, id);
        return findAdminByIdSql(id);
    }

    public Page<Admin> getAdminPage(AdminPageVO vo) {
        Pageable pageable = BackendServerUtil.setPageable(vo.getPageNumber() - 1, vo.getPageSize());
        Specification<Admin> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StringUtils.isNotBlank(vo.getUserName())) {
                predicates.add(cb.equal(root.get("userName"), vo.getUserName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<Admin> page = adminRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    @Cacheable
    public Admin findAdminById(Long id) {
        Optional<Admin> optional = adminRepository.findById(id);
        return optional.orElse(null);
    }

    @CachePut(key = "#id")
    public Admin updatePasswordById(String password, Long id) {
        adminRepository.updateAdminPasswordById(password, id);
        return findAdminByIdSql(id);
    }

    private Admin findAdminByIdSql(Long id) {
        Optional<Admin> optional = adminRepository.findById(id);
        return optional.orElse(null);
    }


}
