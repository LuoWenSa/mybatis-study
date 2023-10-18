package com.luo.dao;

import com.luo.pojo.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    @Select("select id, name, pwd as password from user")
    List<User> getUsers();

}
