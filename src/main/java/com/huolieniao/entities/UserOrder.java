package com.huolieniao.entities;

import lombok.Data;

import java.util.Date;

@Data
public class UserOrder {
    // 主键
    private Long id;
    // 昵称
    private String nickname;
    // 密码
    private String password;
    // 年龄
    private Integer age;
    // 性别
    private Integer sex;
    // 生日
    private Date birth;
    private String ordername;
}
