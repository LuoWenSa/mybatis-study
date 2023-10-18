package com.luo.dao;

import com.luo.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    List<User> getUserLike(String value);

    //查询全部用户
    List<User> getUserList();

    //根据id查询用户
    User getUserById(Integer id);

    User getUser2(@Param("id") Integer id, @Param("name") String name);

    //insert一个用户
    int addUser(User user);

    //万能的Map
    int addUser2(Map<String, Object> map);

    //修改用户
    int updateUser(User user);

    //删除一个用户
    int deleteUser(Integer id);
}
