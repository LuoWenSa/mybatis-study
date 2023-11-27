package com.luo.dao;

import com.luo.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    //根据id查找用户
    User queryUserById(@Param("id") Integer id);

    int updateUser(User user);
}
