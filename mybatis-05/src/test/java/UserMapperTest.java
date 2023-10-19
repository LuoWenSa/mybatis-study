import com.luo.dao.UserMapper;
import com.luo.pojo.User;
import com.luo.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class UserMapperTest {

    @Test
    public void test(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();

        //底层主要应用反射
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        /*
        List<User> users = userMapper.getUsers();
        System.out.println("users = " + users);
         */
        /* select
        User user = userMapper.getUserByID(3);
        System.out.println("user = " + user);
         */
        /* update
        User user = new User(6,"大熊","zoozoo");
        userMapper.addUser(user);
         */
        /*
        User user = new User(6,"熊大","zoozoo");
        userMapper.updateUser(user);
         */
        userMapper.deleteUserById(6);

        sqlSession.close();
    }
}
