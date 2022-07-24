package com.baisha.userserver.service;

import com.baisha.userserver.model.User;
import com.baisha.userserver.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author yihui
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "user::info")
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EntityManager entityManager;


    public void doFlushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    public void doRefresh(User user) {
        entityManager.refresh(user);
    }

    @Caching(put = {@CachePut(key = "#user.id")})
//    @CachePut(key = "#p0.id")
    public User saveUser(User user) {
        userRepository.save(user);
        return user;
    }


    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User findByTgUserId(String tgUserId) {
        return userRepository.findByTgUserId(tgUserId);
    }

    public Page<User> getUserPage(Specification<User> spec, Pageable pageable) {
        Page<User> page = userRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    @Cacheable(key = "#id", unless = "#result == null")
    public User findById(Long id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            User user = optional.get();
            return user;
        }
        return null;
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    //@Caching(put = {@CachePut(key = "#id")})
    @CachePut(key = "#id", unless = "#result == null")
    public User statusById(Integer status, Long id) {
        int i = userRepository.updateUserStatusById(status, id);
        if (i > 0) {
            //entityManager.flush();
            //entityManager.clear();
            return userRepository.findById(id).get();
        }
        return null;
    }


    public List<User> findByUserType(Integer userType) {
        return userRepository.findByUserType(userType);
    }

    public List<User> findByIdIn(List<Long> userIds) {
        return userRepository.findByIdIn(userIds);
    }

    @CachePut(key = "#id", unless = "#result == null")
    public User updateUserType(Long id, Integer userType) {
        int i = userRepository.updateUserType(userType, id);
        log.info("i={}", i);
        if (i > 0) {
            User user = userRepository.findById(id).get();
            doRefresh(user);
            return user;
        }

        return null;
    }


}
