package com.baisha.userserver.service;

import com.baisha.userserver.model.UserTelegramRelation;
import com.baisha.userserver.repository.UserTelegramRelationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class UserTelegramRelationService {

    @Autowired
    private UserTelegramRelationRepository relationRepository;


    public UserTelegramRelation findByUserIdAndTgGroupId(Long userId, String tgGroupId) {
        return relationRepository.findByUserIdAndTgGroupId(userId, tgGroupId);
    }

    public UserTelegramRelation save(UserTelegramRelation userTelegramRelation) {
        relationRepository.save(userTelegramRelation);
        return userTelegramRelation;
    }

    public Page<UserTelegramRelation> getUserTelegramPage(Specification<UserTelegramRelation> spec, Pageable pageable) {
        Page<UserTelegramRelation> page = relationRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }
}
