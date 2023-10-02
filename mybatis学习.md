# 1. 简介

mybatis中文文档：https://mybatis.org/mybatis-3/zh/index.html

## 1.1 什么是mybatis

![](http://www.mybatis.org/images/mybatis-logo.png)

- MyBatis 是一款优秀的**持久层框架**。
- 它支持自定义 SQL、存储过程以及高级映射。
- MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。
- MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。

- MyBatis本是apache的一个[开源项目](https://baike.baidu.com/item/开源项目/3406069?fromModule=lemma_inlink)**iBatis**（Internet abatis），2010年这个项目由apache software foundation迁移到了[google code](https://baike.baidu.com/item/google code/2346604?fromModule=lemma_inlink)，并且改名为MyBatis。2013年11月迁移到[Github](https://baike.baidu.com/item/Github/10145341?fromModule=lemma_inlink)。

## 1.2 如何获取mybatis

- github：https://github.com/mybatis/mybatis-3

- maven：

  ```xml
  <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
  <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.5.13</version>
  </dependency>
  ```

  

## 1.3 什么是持久化

数据持久化

- 持久化就是将程序的数据在**持久状态**和**瞬时状态**转化的过程
- 持久化方式：数据库jdbc，io文件持久化

## 1.4 什么是持久层

Dao层，Service层，Controller层...

- 完成持久化工作的代码

## 1.5 优点

- 方便
- 传统的JDBC代码太复杂了。简化。框架。自动化。

# 2.第一个Mybatis程序

思路：搭建环境-->导入Mybatis-->编写代码-->测试！

## 2.1 搭建环境

搭建数据库

```sql
CREATE DATABASE mybatis;

use mybatis;

CREATE TABLE user(
	id INT(20) NOT NULL PRIMARY KEY,
    name VARCHAR(30) DEFAULT NULL,
    pwd VARCHAR(30) DEFAULT NULL
)ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT INTO user(id,name,pwd) VALUES
(1,'小罗','123456'),
(2,'小鱼','159753'),
(3,'小雪','200115')
```

新建项目

1.新建一个普通的maven 项目

2.删除src目录

3.导入maven依赖

```xml
<!--导入依赖-->
<dependencies>
    <!--mysql驱动-->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.47</version>
    </dependency>
    <!--mybatis-->
    <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.5.13</version>
    </dependency>
    <!--junit-->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 2.2 创建一个模块

- 编写mybatis的核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-config.dtd">
<!--configuration核心配置文件-->
<configuration>

    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=Asia/Shanghai"/>
                <property name="username" value="root"/>
                <property name="password" value="123"/>
            </dataSource>
        </environment>
    </environments>
    
    <!--每一个Mapper.xml都需要在Mybatis核心配置文件中注册！-->
    <mappers>
        <mapper resource="com/luo/dao/UserMapper.xml"/>
    </mappers>

</configuration>
```

- 编写mybatis工具类

```java
//sqlSessionFactory --> sqlSession
public class MybatisUtil {

    //提升作用域
    private static SqlSessionFactory sqlSessionFactory;

//来自官方代码
    static {
        try {
            //使用Mybatis第一步：获取sqlSessionFactory对象
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //既然有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例。
    //SqlSession 提供了在数据库执行 SQL 命令所需的所有方法。你可以通过 SqlSession 实例来直接执行已映射的 SQL 语句
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }
}
```

## 2.3 编写代码

- 实体类

```java
public class User {
    private Integer id;
    private String name;
    private String pwd;

    public User() {
    }

    public User(Integer id, String name, String pwd) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
```

- Dao接口

```java
public interface UserDao {
    List<User> getUserList();
}
```

- 接口实现类由原来的UserDaoImpl转变为一个Mapper配置文件

```xml-dtd
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace=绑定一个对应的Dao/Mapper接口-->
<mapper namespace="com.luo.dao.UserMapper">

<!--select查询语句-->
    <select id="getUserList" resultType="com.luo.pojo.User">
        select
            *
        from user
    </select>

</mapper>
```

## 2.4 测试

**注意点：**<font color="red">org.apache.ibatis.binding.BindingException: Type interface com.luo.dao.UserMapper is not known to the MapperRegistry.</font>

**每一个Mapper.xml都需要在Mybatis核心配置文件中注册！**

```xml-dtd
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-config.dtd">
<!--configuration核心配置文件-->
<configuration>

	......
    
    <!--每一个Mapper.xml都需要在Mybatis核心配置文件中注册！-->
    <mappers>
        <mapper resource="com/luo/dao/UserMapper.xml"/>
    </mappers>

</configuration>
```

**注意点：**<font color="red">Caused by: java.io.IOException: Could not find resource com/luo/dao/UserMapper.xml</font>

```xml-dtd
<!--在build中配置resource，来防止我们资源导出失败的问题-->
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

**注意点：**<font color="red">Cause: org.apache.ibatis.builder.BuilderException: Error parsing SQL Mapper Configuration. Cause: org.apache.ibatis.builder.BuilderException: Error creating document instance.  Cause: com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException: 1 字节的 UTF-8 序列的字节 1 无效。</font>

1. xml文件有中文注释会报错，将有中文注释的xml的<?xml version="1.0" encoding="UTF-8" ?>改为<?xml version="1.0" encoding="UTF8" ?>解决
2. 将项目的编码改为UTF-8在pom文件中增加

```xml-dtd
<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**注意点：**<font color="red">mapper的xml文件配置完后不变色</font>

```xml-dtd
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
		"https://mybatis.org/dtd/mybatis-3-mapper.dtd">  <!-- 这是官网自带的，有https -->

<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
		"http://mybatis.org/dtd/mybatis-3-mapper.dtd">  <!-- 将https改为http就行 -->
```



- junit测试

```java
public class UserDaoTest {
    @Test
    public void test(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        //第二步：执行SQL
        //方式一：getMapper
        UserDao userMapper = sqlSession.getMapper(UserDao.class); //面向接口编程，多态
        List<User> userList = userMapper.getUserList();
        System.out.println("userList = " + userList);
        //方式二：不推荐使用
        List<User> userList = sqlSession.selectList("com.luo.dao.UserMapper.getUserList");
        System.out.println("userList = " + userList);

        //关闭sqlSession，当然也可以让jvm自己处理
        sqlSession.close();
    }
}
```

#### SqlSessionFactoryBuilder

这个类可以被实例化、使用和丢弃，**一旦创建了 SqlSessionFactory，就不再需要它了**。 因此 SqlSessionFactoryBuilder 实例的最佳作用域是方法作用域（也就是局部方法变量）。 你可以重用 SqlSessionFactoryBuilder 来创建多个 SqlSessionFactory 实例，但最好还是不要一直保留着它，以保证所有的 XML 解析资源可以被释放给更重要的事情。

#### SqlSessionFactory

SqlSessionFactory **一旦被创建就应该在应用的运行期间一直存在**，没有任何理由丢弃它或重新创建另一个实例。 使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码“坏习惯”。因此 SqlSessionFactory 的最佳作用域是应用作用域。 有很多方法可以做到，最简单的就是使用**单例模式或者静态单例模式**。

#### SqlSession

每个线程都应该有它自己的 SqlSession 实例。**SqlSession 的实例不是线程安全的**，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。 绝对不能将 SqlSession 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 HttpSession。 如果你现在正在使用一种 Web 框架，考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。 换句话说，**每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它。** 这个关闭操作很重要，为了确保每次都能执行关闭操作，你应该把这个关闭操作放到 finally 块中。 下面的示例就是一个确保 SqlSession 关闭的标准模式：

# 3.CRUD

*CRUD*是指在做计算处理时的增加(Create)、读取(Read)、更新(Update)和删除(Delete)几个单词的首字母简写

<font color = "red">**提交事务(增、删、改必须得提交事务)  sqlSession.commit();**</font>

**以updateUser为例：**

1.编写接口

```java
public interface UserMapper {
    ...
    //修改用户
    int updateUser(User user);
    ...
}
```

2.编写对应的mapper中的sql语句

```xml
<update id="updateUser" parameterType="com.luo.pojo.User">
        update user
        set name = #{name},
            pwd = #{pwd}
        where id = #{id}
</update>
```

3.测试

```java
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
```

## 3.1 namespace

*mapper.xml层的namespace中的包名，例：<mapper namespace="com.luo.dao.UserMapper">，应该于Dao/Mapper接口层的包名一致

## 3.2 select

- id：就是对应的namespace中的方法名；
- resultType：Sql语句执行的返回值！
- parameterType：参数类型！

```xml
<select id="getUserById" parameterType="integer" resultType="com.luo.pojo.User">
    select
    	*
    from user
    where id = #{id}
</select>
```

## 3.3 Insert

```xml
<!--对象中属性，可以直接取出来-->
<insert id="addUser" parameterType="com.luo.pojo.User">
    insert into user(id, name, pwd)
    values (#{id}, #{name}, #{pwd})
</insert>
```

## 3.4 Update

```xml
<update id="updateUser" parameterType="com.luo.pojo.User">
    update user
    set name = #{name},
    	pwd = #{pwd}
    where id = #{id}
</update>
```

## 3.5 Delete

```xml
<delete id="deleteUser" parameterType="integer">
    delete from user
    where id = #{id}
</delete>
```

## 3.7 万能的Map

假设，我们的实体类，或者数据库中的表，字段或者参数过多，我们应当考虑使用Map！

```java
//万能的Map
int addUser2(Map<String, Object> map);
```

```xml
<insert id="addUser2" parameterType="map">
    insert into user (id, name, pwd)
    values (#{userId}, #{userName}, #{password})
</insert>
```

**多个参数可以用Map，或者注解！**

## 3.8 模糊查询

1.java代码执行的时候，传递通配符% %

```java
List<User> userList = userMapper.getUserLike("大%");
---------------------------------------------------
select
	*
from user
where name like #{value}
```

2.在sql拼接中使用通配符！防止sql注入

```mysql
List<User> userList = userMapper.getUserLike("大");
-----------------------------------------------
select
	*
from user
where name like concat('%', #{value}, '%')
```

### SQL注入

```mysql
select * from user where id = ?   <!-应该传：1，获取id=1的用户-->
select * from user where id = 1 or 1=1 <!-实际传了：1 or 1=1，获取所有用户-->
```

# 4.配置解析

## 4.1 核心配置文件

- mybatis-config.xml
- MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息。

```xml
configuration（配置）
properties（属性）
settings（设置）
typeAliases（类型别名）
typeHandlers（类型处理器）
objectFactory（对象工厂）
plugins（插件）
environments（环境配置）
environment（环境变量）
transactionManager（事务管理器）
dataSource（数据源）
databaseIdProvider（数据库厂商标识）
mappers（映射器）
```

## 4.2 环境配置（environments）

MyBatis 可以配置成适应多种环境

**不过要记住：尽管可以配置多个环境，但每个SqlSessionFactory实例只能选择一种环境。**

Mybatis默认的事务管理器就是JDBC，连接池就是：POOLED <font color = "red">**（”池“的意思就是用完还可以回收再次利用）**</font>

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--configuration核心配置文件-->
<configuration>

    <environments default="test">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=Asia/Shanghai"/>
                <property name="username" value="root"/>
                <property name="password" value="123"/>
            </dataSource>
        </environment>

        <environment id="test">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=Asia/Shanghai"/>
                <property name="username" value="root"/>
                <property name="password" value="123"/>
            </dataSource>
        </environment>
    </environments>
...

</configuration>
```

## 4.3 属性（properties）

我们可以通过properties属性来实现引用配置文件

这些属性可以在外部进行配置，并可以进行动态替换。你既可以在典型的 Java 属性文件中配置这些属性，也可以在 properties 元素的子元素中设置。【db.properties】

1.编写一个配置文件

db.properties

```properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?useSSL=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
username=root
password=123
```

2.在核心配置文件中引用

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--configuration核心配置文件-->
<configuration>

    <!--引入外部配置文件-->
    <properties resource="db.properties"/>  //第一种
     <!--
    <properties resource="db.properties">  //第二种
        <property name="username" value="root"/>
        <property name="password" value="111111"/>
    </properties>
	-->

    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>

...

</configuration>
```

- 可以直接引入外部文件
- 可以在其中增加一些属性配置
- **如果两个文件有同一个字段，优先使用外部配置文件的！**

## 4.4 类型别名（typeAliases）

- 类型别名可为 Java 类型设置一个缩写名字。
- 存在的意义仅在降低冗余的全限定类名书写的冗余

**方式一：**

给一个实体类单独起名

```xml
<!--mybatis-config.xml-->
<!--可以给实体类起别名-->
<typeAliases>
    <typeAlias type="com.luo.pojo.User" alias="User"/>
</typeAliases>
-------------------------------------------------------
<!--UserMapper.xml-->
<select id="getUserList" resultType="User">
    select
    	*
    from user
</select>
```

**方式二：**

指定一个包名，Mybatis会在包名下面搜索需要的Java Bean，比如：扫描实体类的包，它的默认别名就为这个类的 类名，首字母小写*(大写也可)*！

```xml
<!--mybatis-config.xml-->
<!--扫描指定包下的实体类们，给它们默认别名为类名首字母小写-->
<typeAliases>
    <package name="com.luo.pojo"/>
</typeAliases>
----------------------------------------------------
<!--UserMapper.xml-->
<select id="getUserList" resultType="user">
    select
    	*
    from user
</select>
```

在实体类比较少的时候，使用第一种方式。

如果实体类十分多，建议使用第二种。

第一种可以DIY别名，第二种则.不行.，如果非要改，需要在实体上增加注解

```xml
@Alias("userA")
public class User {
    ......
}
--------------------
<!--UserMapper.xml-->
<select id="getUserList" resultType="userA">
    select
    	*
    from user
</select>
```

**Java 类型内建的类型别名**

| 别名       | 映射的类型 |
| :--------- | :--------- |
| _int       | int        |
| decimal    | BigDecimal |
| bigdecimal | BigDecimal |
| map        | Map        |
| int        | Integer    |

。。。

## 4.5 设置（settings）

这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。

```xml
<settings>
  <setting name="cacheEnabled" value="true"/> //开启缓存
  <setting name="lazyLoadingEnabled" value="true"/> //懒加载
  <setting name="aggressiveLazyLoading" value="true"/>
  <setting name="multipleResultSetsEnabled" value="true"/>
  <setting name="useColumnLabel" value="true"/>
  <setting name="useGeneratedKeys" value="false"/>
  <setting name="autoMappingBehavior" value="PARTIAL"/>
  <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
  <setting name="defaultExecutorType" value="SIMPLE"/>
  <setting name="defaultStatementTimeout" value="25"/>
  <setting name="defaultFetchSize" value="100"/>
  <setting name="safeRowBoundsEnabled" value="false"/>
  <setting name="safeResultHandlerEnabled" value="true"/>
  <setting name="mapUnderscoreToCamelCase" value="false"/> //orm字段驼峰转换
  <setting name="localCacheScope" value="SESSION"/>
  <setting name="jdbcTypeForNull" value="OTHER"/>
  <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
  <setting name="defaultScriptingLanguage" value="org.apache.ibatis.scripting.xmltags.XMLLanguageDriver"/>
  <setting name="defaultEnumTypeHandler" value="org.apache.ibatis.type.EnumTypeHandler"/>
  <setting name="callSettersOnNulls" value="false"/>
  <setting name="returnInstanceForEmptyRow" value="false"/>
  <setting name="logPrefix" value="exampleLogPreFix_"/>
  <setting name="logImpl" value="SLF4J | LOG4J | LOG4J2 | JDK_LOGGING | COMMONS_LOGGING | STDOUT_LOGGING | NO_LOGGING"/> //实现日志功能
  <setting name="proxyFactory" value="CGLIB | JAVASSIST"/>
  <setting name="vfsImpl" value="org.mybatis.example.YourselfVfsImpl"/>
  <setting name="useActualParamName" value="true"/>
  <setting name="configurationFactory" value="org.mybatis.example.ConfigurationFactory"/>
</settings>
```

## 4.6 其他配置

- [typeHandlers（类型处理器）](https://mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)
- [objectFactory（对象工厂）](https://mybatis.org/mybatis-3/zh/configuration.html#objectFactory)
- [plugins（插件）](https://mybatis.org/mybatis-3/zh/configuration.html#plugins)
  - mybatis-generator-core
  - mybatis-plus
  - 通用mapper

## 4.7 映射器（mappers）

MapperRegistry：注册绑定我们的Mapper文件；

方式一：使用**xml路径**<font color = "red">【推荐使用】</font>

```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
  <mapper resource="com/luo/dao/UserMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>
```

方式二：使用class文件绑定注册（**接口类路径**）

```xml
<mappers>
    <mapper class="com.luo.dao.UserMapper"/>
</mappers>
```

<font color = "red">**注意点：**</font>

- <font color = "red">**接口和他的Mapper配置文件必须同名！**</font>
- <font color = "red">**接口和他的Mapper配置文件必须在同一个包下！**</font>

方式三：使用扫描包进行注入绑定

```xml
<!-- 将包内的映射器接口全部注册为映射器 -->
<mappers>
  <package name="com.luo.dao"/>
</mappers>
```

<font color = "red">**注意点：**</font>

- <font color = "red">**接口和他的Mapper配置文件必须同名！**</font>
- <font color = "red">**接口和他的Mapper配置文件必须在同一个包下！**</font>