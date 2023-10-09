package com.luo.dao;

import com.luo.pojo.User;

import java.util.List;

public interface UserMapper {

    //根据id查询用户
    User getUserById(Integer id);

}
