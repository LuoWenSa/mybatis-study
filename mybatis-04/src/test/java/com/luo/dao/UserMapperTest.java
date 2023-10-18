package com.luo.dao;

import com.luo.pojo.User;
import com.luo.utils.MybatisUtil;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMapperTest {

    static Logger logger = Logger.getLogger(UserMapperTest.class);

    @Test
    public void getUserByRowBounds(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();

        //RowBounds实现
        RowBounds rowBounds = new RowBounds(1,2);

        //通过Java代码层面实现分页
        List<User> userList = sqlSession.selectList("com.luo.dao.UserMapper.getUserByRowBounds",null,rowBounds);
        System.out.println("userList = " + userList);

        sqlSession.close();
    }

    @Test
    public void getUserByLimit(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        Map<String, Integer> map = new HashMap<>();
        map.put("startIndex",1);
        map.put("pageSize",2);
        //分页
        List<User> userList = userMapper.getUserByLimit(map);
        System.out.println("userList = " + userList);

        sqlSession.close();
    }

    @Test
    public void test(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        //第二步：执行SQL
        //方式一：getMapper

        logger.info("测试，进入getUserById方法成功！");

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class); //面向接口编程，多态

        User user = userMapper.getUserById(2);
        System.out.println("user = " + user);
        //方式二：不推荐使用
//        List<User> userList = sqlSession.selectList("com.luo.dao.UserDao.getUserList");
//        System.out.println("userList = " + userList);

        //关闭sqlSession，当然也可以让jvm自己处理
        sqlSession.close();
    }

    @Test
    public void testLog4j(){
        logger.info("info:进入了testLog4j");
        logger.debug("debug:进入了testLog4j");
        logger.error("error:进入了testLog4j");
    }

}
