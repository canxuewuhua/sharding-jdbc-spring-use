package com.huolieniao.entities;

import lombok.Data;
import java.util.Date;

@Data
public class User {

    // 主键
    private Long id;
    // 昵称
    private String nickname;
    // 密码
    private String password;
    // 性
    private Integer sex;
    // 性
    private String birthday;
    private Date createTime;
    private Date updateTime;
}
