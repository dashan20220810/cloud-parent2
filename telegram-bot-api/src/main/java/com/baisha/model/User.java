package com.baisha.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class User extends BaseEntity{

    private String name;
    private String password;
    @Column
    private String nickname;

    public static boolean checkName() {
        return false;
    }

    public static boolean chekUser() {
        //1.查检是否被禁 用
        //2.检查是否在黑名单用户
        return false;
    }

}
