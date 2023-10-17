package com.luo.dao;

import com.luo.pojo.User;
import com.luo.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.Test;

public class UserMapperTest {

    static Logger logger = Logger.getLogger(UserMapperTest.class);

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
