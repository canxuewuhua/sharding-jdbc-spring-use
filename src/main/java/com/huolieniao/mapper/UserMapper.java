package com.huolieniao.mapper;

import com.huolieniao.entities.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("insert into user(nickname,password,sex,birthday,create_time,update_time) values(#{nickname},#{password},#{sex},#{birthday},#{createTime},#{updateTime})")
    void addUser(User user);

    @Select("select * from user")
    List<User> findUsers();
}
