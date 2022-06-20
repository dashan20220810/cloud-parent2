package com.baisha.repository;

import com.baisha.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByName(String name);
    User findByNameAndPassword(String name, String password);

    @Query(value = "update User  u set u.nickname=?1 where u.id=?2")
    @Modifying
    void updateNicknameById(String nickname, Long id);
}
