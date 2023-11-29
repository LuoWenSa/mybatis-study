import com.luo.dao.UserMapper;
import com.luo.pojo.User;
import com.luo.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class MyTest {

    @Test
    public void test(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        User user = userMapper.queryUserById(3);
        System.out.println("user = " + user);

        //中间更新，一级缓存失效
        //userMapper.updateUser(new User(1,"小罗","1234567"));

        //手动清理缓存
        //sqlSession.clearCache();

        System.out.println("=========================");

        User user2 = userMapper.queryUserById(3);
        System.out.println("user2 = " + user2);

        System.out.println(user == user2);

        sqlSession.close();
    }

    @Test
    public void test2(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        SqlSession sqlSession2 = MybatisUtil.getSqlSession();

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);

        User user = userMapper.queryUserById(3);
        System.out.println("user = " + user);
        sqlSession.close();

        User user2 = userMapper2.queryUserById(3);
        System.out.println("user2 = " + user2);
        sqlSession2.close();
    }

}
