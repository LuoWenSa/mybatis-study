import com.luo.dao.BlogMapper;
import com.luo.pojo.Blog;
import com.luo.utils.IDUtils;
import com.luo.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.*;

public class MyTest {
    @Test
    public void addInitBlog(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);

        Blog blog = new Blog();
        blog.setId(IDUtils.getId());
        blog.setTitle("Mybatis");
        blog.setAuthor("狂神说");
        blog.setCreateTime(new Date());
        blog.setViews(9999);

        mapper.addBlog(blog);

        blog.setId(IDUtils.getId());
        blog.setTitle("Java");
        mapper.addBlog(blog);

        blog.setId(IDUtils.getId());
        blog.setTitle("Spring");
        mapper.addBlog(blog);

        blog.setId(IDUtils.getId());
        blog.setTitle("微服务");
        mapper.addBlog(blog);

        sqlSession.close();
    }

    @Test
    public void queryBlogIF(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);

        Map<String, Object> map = new HashMap<>();
        map.put("title","Java");
        map.put("author", "狂神说");
        List<Blog> blogs = mapper.queryBlogIF(map);
        System.out.println("blogs = " + blogs);

//        Map<String, Object> map = new HashMap<>();
//        map.put("title","");
//        List<Blog> blogs = mapper.queryBlogChoose(map);
//        System.out.println("blogs = " + blogs);

        sqlSession.close();
    }

    @Test
    public void updateBlogSet(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);

        Map<String, Object> map = new HashMap<>();
        map.put("title","Java2");
        map.put("author", "狂神说");
        map.put("id","1");
        int flag = mapper.updateBlog(map);

        sqlSession.close();
    }

    @Test
    public void queryBlogForEach(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);

//        Map<String, Object> map = new HashMap<>();
        List<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
//        map.put("idss",ids);
//        map.put("id1","1");
//        map.put("id2","2");
//        map.put("id3","3");
        List<Blog> blogs = mapper.queryBlogForeach(ids);
        System.out.println("blogs = " + blogs);

        sqlSession.close();

    }

}
