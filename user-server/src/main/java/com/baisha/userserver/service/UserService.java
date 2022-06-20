package com.baisha.userserver.service;

import com.baisha.userserver.constants.UserServerConstants;
import com.baisha.userserver.model.Assets;
import com.baisha.userserver.model.User;
import com.baisha.userserver.repository.AssetsRepository;
import com.baisha.userserver.repository.UserRepository;
import com.baisha.userserver.vo.user.UserPageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author yihui
 */
@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "user")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @CachePut(key = "#p0.id")
    public User saveUser(User user) {
        userRepository.save(user);
        return user;
    }


    @Cacheable
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User findByUserNameSql(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Page<User> getUserPage(UserPageVO vo) {
        Pageable pageable = PageRequest.of(vo.getPageNumber() - 1, vo.getPageSize());
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(cb.equal(root.get("isDelete"), UserServerConstants.DELETE_NORMAL));
            if (StringUtils.isNotBlank(vo.getUserName())) {
                predicates.add(cb.equal(root.get("userName"), vo.getUserName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<User> page = userRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    @CacheEvict
    public void doDelete(Long id) {
        userRepository.deleteUserById(id);
    }


    public void doStatus(Integer status, Long id) {
        userRepository.updateUserStatusById(status, id);
    }
}
