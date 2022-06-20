package com.baisha.backendserver.repository;


import com.baisha.backendserver.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author yihui
 */
public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {

    /**
     * 根据用户名查询
     *
     * @param userName
     * @return
     */
    Admin findByUserName(String userName);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @Query(value = "update Admin  u set u.isDelete = 1 where u.id=?1")
    @Modifying
    int deleteAdminById(Long id);

    /**
     * 更新状态
     *
     * @param status
     * @param id
     * @return
     */
    @Query(value = "update Admin  u set u.status = ?1 where u.id=?2")
    @Modifying
    int updateAdminStatusById(Integer status, Long id);

    /**
     * 更新密码
     *
     * @param password
     * @param id
     * @return
     */
    @Query(value = "update Admin  u set u.password = ?1 where u.id=?2")
    @Modifying
    int updateAdminPasswordById(String password, Long id);


}
