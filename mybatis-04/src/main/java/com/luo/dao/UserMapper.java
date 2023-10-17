package com.luo.dao;

import com.luo.pojo.User;

public interface UserMapper {

    //根据id查询用户
    User getUserById(Integer id);

}
