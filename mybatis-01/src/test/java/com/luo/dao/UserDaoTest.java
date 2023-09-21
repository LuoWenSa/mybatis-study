package com.luo.dao;

import com.luo.pojo.User;
import com.luo.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class UserDaoTest {
    @Test
    public void test(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        //第二步：执行SQL
        //方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class); //面向接口编程，多态
        List<User> userList = userDao.getUserList();
        System.out.println("userList = " + userList);
        //方式二：不推荐使用
//        List<User> userList = sqlSession.selectList("com.luo.dao.UserDao.getUserList");
//        System.out.println("userList = " + userList);

        //关闭sqlSession，当然也可以让jvm自己处理
        sqlSession.close();
    }
}
