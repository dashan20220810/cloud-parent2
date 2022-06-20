package com.baisha.userserver.repository;


import com.baisha.userserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author yihui
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 根据用户名查询
     *
     * @param userName
     * @return
     */
    User findByUserName(String userName);

    /**
     * 更新状态
     *
     * @param status
     * @param id
     */
    @Query(value = "update User  u set u.status = ?1 where u.id=?2")
    @Modifying
    int updateUserStatusById(Integer status, Long id);
}
