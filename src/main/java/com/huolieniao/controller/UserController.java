package com.huolieniao.controller;

import com.huolieniao.entities.User;
import com.huolieniao.entities.UserOrder;
import com.huolieniao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/save")
    public String insert() {
        User user = new User();
        user.setNickname("zhangsan"+ new Random().nextInt());
        user.setPassword("1234567");
        user.setAge(2);
        user.setSex(1);
        Date date = new Date();
        user.setBirth(date);
        userMapper.addUser(user);
        return "success";
    }
    @GetMapping("/listuser")
    public List<User> listuser() {
        return userMapper.findUsers();
    }

    @GetMapping("/listuserorder")
    public List<UserOrder> listuserorder() {
        return userMapper.findUserOrders();
    }
}
