package com.huolieniao.mapper;

import com.huolieniao.entities.User;
import com.huolieniao.entities.UserOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("insert into ksd_user(nickname,password,age,sex,birth) values(#{nickname},#{password},#{age},#{sex},#{birth})")
    void addUser(User user);

    @Select("select * from ksd_user")
    List<User> findUsers();

    @Select("select * from ksd_user u left join ksd_order o on u.orderid = o.id")
    List<UserOrder> findUserOrders();
}
