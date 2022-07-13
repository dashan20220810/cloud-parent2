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

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author yihui
 */
@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "user::info")
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
        return optional.orElse(null);
    }

    @CacheEvict
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Caching(put = {@CachePut(key = "#id")})
    public User statusById(Integer status, Long id) {
        int i = userRepository.updateUserStatusById(status, id);
        if (i > 0) {
            return userRepository.findById(id).get();
        }
        return null;
    }



}
