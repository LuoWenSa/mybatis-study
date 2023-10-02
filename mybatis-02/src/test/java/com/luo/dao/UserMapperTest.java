package com.luo.dao;

import com.luo.pojo.User;
import com.luo.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMapperTest {
    @Test
    public void test(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        //第二步：执行SQL
        //方式一：getMapper
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class); //面向接口编程，多态

        List<User> userList = userMapper.getUserList();
        System.out.println("userList = " + userList);
        //方式二：不推荐使用
//        List<User> userList = sqlSession.selectList("com.luo.dao.UserDao.getUserList");
//        System.out.println("userList = " + userList);

        //关闭sqlSession，当然也可以让jvm自己处理
        sqlSession.close();
    }

}
