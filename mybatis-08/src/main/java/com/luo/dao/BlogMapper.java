package com.luo.dao;

import com.luo.pojo.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BlogMapper {
    //插入数据
    int addBlog(Blog blog);

    //查询博客
    List<Blog> queryBlogIF(Map<String, Object> map);

    List<Blog> queryBlogChoose(Map<String, Object> map);

    //更新博客
    int updateBlog(Map<String, Object> map);

    //查询第1-2-3号记录的博客
    List<Blog> queryBlogForeach(@Param("ids") List<String> ids);

}
