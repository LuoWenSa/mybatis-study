package com.luo.dao;

import com.luo.pojo.User;
import com.luo.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

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

    @Test
    public void getUserById(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        //第二步：执行SQL
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.getUserById(3);
        System.out.println("user = " + user);
        //关闭sqlSession
        sqlSession.close();
    }

    @Test
    public void addUser(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        //第二步：执行SQL
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int res = userMapper.addUser(new User(4, "小霖", "098765"));
        if (res > 0) {
            System.out.println("插入成功！");
        } else {
            System.out.println("插入失败！");
        }
        //提交事务(增、删、改必须得提交事务)
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();
    }

    @Test
    public void updateUser(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        //第二步：执行SQL
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int i = userMapper.updateUser(new User(4, "小霖", "lxlwsf"));
        if (i > 0) {
            System.out.println("修改成功！");
        } else {
            System.out.println("修改失败！");
        }
        //提交事务(增、删、改必须得提交事务)
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();
    }

    @Test
    public void deleteUser(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        //第二步：执行SQL
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int i = userMapper.deleteUser(5);
        if(i > 0){
            System.out.println("删除成功！");
        }else {
            System.out.println("删除失败！");
        }
        //提交事务(增、删、改必须得提交事务)
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();

    }

}
