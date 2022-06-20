package com.baisha.service;

import com.baisha.model.User;
import com.baisha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;
    public void save(User user) {
        userRepository.save(user);
    }

    public User findOne(Long id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public void updateNicknameById(String nickname, Long id) {
        userRepository.updateNicknameById(nickname,id);
    }
}
