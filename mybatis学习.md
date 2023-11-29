# 1.简介

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

## 4.8 生命周期和作用域

生命周期和作用域是至关重要的，因为错误的使用会导致非常严重的<font color = "red">并发问题</font>

**SqlSessionFactoryBuilder:**

- 一旦创建了SqlSessionFactory，就不再需要它了
- 局部变量

**SqlSessionFactory：**

- 说白了就是可以想象为：数据库连接池
- SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，**没有任何理由丢弃它或重新创建另一个实例**。
- 因此 SqlSessionFactory 的最佳作用域是应用作用域。**程序开始它就开始，程序结束它就结束**。
- 最简单的就是使用单例模式或者静态单例模式。

**SqlSession：**

- 连接到连接池的一个请求！
- SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。
- 用完之后需要赶紧关闭，否则资源被占用！

# 5.解决属性名和字段名不一致的问题（ResultMap）

- 起别名

```xml
<select id="getUserById" parameterType="integer" resultType="com.luo.pojo.User">
    select
        id, 
        name,
        pwd as password  //重点
    from user
    where id = #{id}
</select>
```

- ResultMap

```xml
<!--结果集映射-->
<resultMap id="UserMap" type="com.luo.pojo.User">
    <!--column数据库中的字段，property实体类中的属性-->
    <result column="id" property="id"/>  <!--这一行可以不要！--> 
    <result column="name" property="name"/> <!--这一行可以不要！-->
    <result column="pwd" property="password"/>
</resultMap>

<select id="getUserById" parameterType="integer" resultMap="UserMap">
    select
    	*
    from user
    where id = #{id}
</select>
```

- resultMap 元素是 MyBatis 中最重要最强大的元素。
- ResultMap 的设计思想是，对简单的语句做到零配置，对于复杂一点的语句，只需要描述语句之间的关系就行了。
- 这就是 ResultMap 的优秀之处——你完全可以不用显式地配置它们。

# 6.日志

## 6.1、日志工厂

如果一个数据库操作，出现了异常，我们需要排错。日志就是最好的助手！

曾经：sout、debug

现在：日志工厂！

| logImpl    | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。 | SLF4J \| LOG4J（3.5.9 起废弃） \| LOG4J2 \| JDK_LOGGING \| COMMONS_LOGGING \| STDOUT_LOGGING \| NO_LOGGING | 未设置     |
| ---------- | ----------------------------------------------------- | ------------------------------------------------------------ | ---------- |
| **设置名** | **描述**                                              | 有效值                                                       | **默认值** |

- SLF4J 
- LOG4J（3.5.9 起废弃）【掌握】
- LOG4J2
- JDK_LOGGING
- COMMONS_LOGGING
- STDOUT_LOGGING【掌握】
- NO_LOGGING

在Mybatis中具体使用哪个----日志实现，在设置中设定！

**STDOUT_LOGGING 标准日志输出**

在mybatis核心配置文件中，配置我们的日志！

```xml
<settings>
    <!--标准的日志工厂实现-->
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```

控制台打印：

```mysql
Opening JDBC Connection
Created connection 1411892748.
Setting autocommit to false on JDBC Connection [com.mysql.jdbc.JDBC4Connection@5427c60c]
==>  Preparing: select * from user where id = ?
==> Parameters: 2(Integer)
<==    Columns: id, name, pwd
<==        Row: 2, 小鱼, 159753
<==      Total: 1
user = User{id=2, name='小鱼', password='159753'}
Resetting autocommit to true on JDBC Connection [com.mysql.jdbc.JDBC4Connection@5427c60c]
Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@5427c60c]
Returned connection 1411892748 to pool.
```

## 6.2、Log4j

什么是Log4j？

- Log4j是[Apache](https://baike.baidu.com/item/Apache/8512995?fromModule=lemma_inlink)的一个[开源项目](https://baike.baidu.com/item/开源项目/3406069?fromModule=lemma_inlink)，通过使用Log4j，我们可以控制日志信息输送的目的地是[控制台](https://baike.baidu.com/item/控制台/2438626?fromModule=lemma_inlink)、文件、GUI组件，甚至是[套接口](https://baike.baidu.com/item/套接口/10058888?fromModule=lemma_inlink)服务器、NT的事件记录器、[UNIX](https://baike.baidu.com/item/UNIX?fromModule=lemma_inlink) [Syslog](https://baike.baidu.com/item/Syslog?fromModule=lemma_inlink)[守护进程](https://baike.baidu.com/item/守护进程/966835?fromModule=lemma_inlink)等

- 我们也可以控制每一条日志的[输出格式](https://baike.baidu.com/item/输出格式/14456488?fromModule=lemma_inlink)

- 通过定义每一条日志信息的级别，我们能够更加细致地控制日志的生成过程

- 通过一个[配置文件](https://baike.baidu.com/item/配置文件/286550?fromModule=lemma_inlink)来灵活地进行配置，而不需要修改应用的代码

  

1.先导入log4j的包

```xml
<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

2.log4j.properties


```properties
#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console,file

#控制台输出的相关设置
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%c]-%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File=./log/luo.log
log4j.appender.file.MaxFileSize=10mb
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```

3.配置log4j为日志的实现

```xml
<settings>
    <setting name="logImpl" value="LOG4J"/>
</settings>
```

4.Log4j的使用！直接测试运行刚才的查询

```mysql
[org.apache.ibatis.logging.LogFactory]-Logging initialized using 'class org.apache.ibatis.logging.log4j.Log4jImpl' adapter.
[org.apache.ibatis.logging.LogFactory]-Logging initialized using 'class org.apache.ibatis.logging.log4j.Log4jImpl' adapter.
[org.apache.ibatis.io.VFS]-Class not found: org.jboss.vfs.VFS
[org.apache.ibatis.io.JBoss6VFS]-JBoss 6 VFS API is not available in this environment.
[org.apache.ibatis.io.VFS]-Class not found: org.jboss.vfs.VirtualFile
[org.apache.ibatis.io.VFS]-VFS implementation org.apache.ibatis.io.JBoss6VFS is not valid in this environment.
[org.apache.ibatis.io.VFS]-Using VFS adapter org.apache.ibatis.io.DefaultVFS
[org.apache.ibatis.io.DefaultVFS]-Find JAR URL: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-04/target/classes/com/luo/pojo
[org.apache.ibatis.io.DefaultVFS]-Not a JAR: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-04/target/classes/com/luo/pojo
[org.apache.ibatis.io.DefaultVFS]-Reader entry: User.class
[org.apache.ibatis.io.DefaultVFS]-Listing file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-04/target/classes/com/luo/pojo
[org.apache.ibatis.io.DefaultVFS]-Find JAR URL: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-04/target/classes/com/luo/pojo/User.class
[org.apache.ibatis.io.DefaultVFS]-Not a JAR: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-04/target/classes/com/luo/pojo/User.class
[org.apache.ibatis.io.DefaultVFS]-Reader entry: ����   4 <
[org.apache.ibatis.io.ResolverUtil]-Checking to see if class com.luo.pojo.User matches criteria [is assignable to Object]
[org.apache.ibatis.datasource.pooled.PooledDataSource]-PooledDataSource forcefully closed/removed all connections.
[org.apache.ibatis.datasource.pooled.PooledDataSource]-PooledDataSource forcefully closed/removed all connections.
[org.apache.ibatis.datasource.pooled.PooledDataSource]-PooledDataSource forcefully closed/removed all connections.
[org.apache.ibatis.datasource.pooled.PooledDataSource]-PooledDataSource forcefully closed/removed all connections.
[org.apache.ibatis.transaction.jdbc.JdbcTransaction]-Opening JDBC Connection
[org.apache.ibatis.datasource.pooled.PooledDataSource]-Created connection 1500608548.
[org.apache.ibatis.transaction.jdbc.JdbcTransaction]-Setting autocommit to false on JDBC Connection [com.mysql.jdbc.JDBC4Connection@59717824]
[com.luo.dao.UserMapper.getUserById]-==>  Preparing: select * from user where id = ?
[com.luo.dao.UserMapper.getUserById]-==> Parameters: 2(Integer)
[com.luo.dao.UserMapper.getUserById]-<==      Total: 1
user = User{id=2, name='小鱼', password='159753'}
[org.apache.ibatis.transaction.jdbc.JdbcTransaction]-Resetting autocommit to true on JDBC Connection [com.mysql.jdbc.JDBC4Connection@59717824]
[org.apache.ibatis.transaction.jdbc.JdbcTransaction]-Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@59717824]
[org.apache.ibatis.datasource.pooled.PooledDataSource]-Returned connection 1500608548 to pool.
```

**简单使用**

1.在要使用Log4j的类中，导入包 import org.apache.log4j.Logger;

2.日志对象，参数为当前类的class

```java
static Logger logger = Logger.getLogger(UserMapperTest.class);
```

3.日志级别

==OFF > FATAL > **ERROR** > **WARN** > **INFO** > **DEBUG** > TRACE > ALL==

```java
logger.info("info:进入了testLog4j");
logger.debug("debug:进入了testLog4j");
logger.error("error:进入了testLog4j");
```

# 7.分页

**思考：为什么要分页？**

- 减少数据的处理量

## 7.1、使用Limit分页

```mysql
语法：SELECT * FROM user LIMIT startIndex,pageSize;
SELECT * FROM user limit 3;  #等价于[0,n]
```



使用Mybatis实现分页，核心SQL

​	1.接口

```java
//分页
List<User> userList = userMapper.getUserByLimit(map);
```

​	2.Mapper.xml

```xml
<!--分页-->
<select id="getUserByLimit" parameterType="map" resultType="User">
    select
        *
    from user
    limit #{startIndex},#{pageSize}
</select>
```

​	3.测试

```java
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
```

## 7.2、RowBounds分页

不再使用SQL实现分页

​	1.接口

```java
//分页
List<User> getUserByRowBounds();
```

​	2.mapper.xml

```xml
<!--分页2-->
<select id="getUserByRowBounds" resultMap="UserMap">
    select
    	*
    from user
</select>
```

​	3.测试

```java
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
```

## 7.3、分页插件

### Mybatis分页插件PageHelper

了解即可，万一以后公司的架构师说要使用，你需要 知道它是什么东西！

# 8.使用注解开发

​	1.注解在接口上实现

```java
@Select("select id, name, pwd as password from user")
List<User> getUsers();
```

​	2.需要在核心配置文件中绑定接口！

```xml
<!--绑定接口-->
<mappers>
    <mapper class="com.luo.dao.UserMapper"/>
</mappers>
```

​	3.测试

```java
@Test
public void test(){
    SqlSession sqlSession = MybatisUtil.getSqlSession();

    //底层主要应用反射
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    List<User> users = userMapper.getUsers();
    System.out.println("users = " + users);

    sqlSession.close();
}
```

本质：反射机制实现

底层：动态代理！

## 8.1 CRUD

我们可以在工具类创建的时候实现自动提交事务！

```java
public static SqlSession getSqlSession(){
    return sqlSessionFactory.openSession(true);
}
```

编写接口，增加注解

```java
public interface UserMapper {

    @Select("select id, name, pwd as password from user")
    List<User> getUsers();

    //方法存在多个参数，所有的参数前面必须加上 @Param("id")
    @Select("select * from user where id = #{id}")
    User getUserByID(@Param("id") int id);

    @Insert("insert into user(id,name,pwd) values(#{id},#{name},#{password})")
    int addUser(User user);

    @Update("update user set name=#{name},pwd=#{password} where id=#{id}")
    int updateUser(User user);

    @Delete("delete from user where id = #{uid}")
    int deleteUserById(@Param("uid") int id);

}
```

【注意：我们必须要将接口注册绑定到我们的核心配置文件中！】

## 关于@Param()注解

- 基本类型的参数或者String类型，需要加上
- @Param("user") User u         #{user.userName},#{user.userAge}
- 如果只有一个基本类型的话，可以忽略，但是建议大家都加上！
- 我们在SQL中引用的就是我们这里的@Param("uid")中设定的属性名！

# Mybatis执行流程剖析

1. Resources获取加载全局配置文件

2. 实例化SqlSessionFactoryBuilder构造器

3. 解析配置文件流XMLConfigBuilder

4. Configuration所有的配置信息（.build(inputStream)）

5. SqlSessionFactory实例化

6. transactional事务管理器，从事务工厂获取一个事务连接

7. **创建executor执行器（import）**

8. 创建sqlSession

9. 实现CRUD

   ---------------------------->失败会回滚事务（6）

   ---------------------------->成功会提交事务（6）

10. 关闭

# 9.Lombok

- java library
- plugs
- build tools
- with one annotation your class

使用步骤：

​	1.在IDEA中安装Lombok插件！

​	2.在项目中导入Lombok的jar包

```xml
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
</dependency>
```

​	3.在实体类上加注解即可！

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String password;
}
```

```
@Getter and @Setter
@FieldNameConstants
@ToString
@EqualsAndHashCode
@AllArgsConstructor, @RequiredArgsConstructor and @NoArgsConstructor
@Log, @Log4j, @Log4j2, @Slf4j, @XSlf4j, @CommonsLog, @JBossLog, @Flogger, @CustomLog
@Data
@Builder
@SuperBuilder
@Singular
@Delegate
@Value
@Accessors
@Wither
@With
@SneakyThrows
@val
@var
experimental @var
@UtilityClass
```

说明：

```java
@Data：无参构造，get，set，toString，hashCode，equals
@AllArgsConstructor
@NoArgsConstructor
```

# 10.多对一、一对多处理

```xml
<!-- 非常复杂的语句 -->
<select id="selectBlogDetails" resultMap="detailedBlogResultMap">
  select
       B.id as blog_id,
       B.title as blog_title,
       B.author_id as blog_author_id,
       A.id as author_id,
       A.username as author_username,
       A.password as author_password,
       A.email as author_email,
       A.bio as author_bio,
       A.favourite_section as author_favourite_section,
       P.id as post_id,
       P.blog_id as post_blog_id,
       P.author_id as post_author_id,
       P.created_on as post_created_on,
       P.section as post_section,
       P.subject as post_subject,
       P.draft as draft,
       P.body as post_body,
       C.id as comment_id,
       C.post_id as comment_post_id,
       C.name as comment_name,
       C.comment as comment_text,
       T.id as tag_id,
       T.name as tag_name
  from Blog B
       left outer join Author A on B.author_id = A.id
       left outer join Post P on B.id = P.blog_id
       left outer join Comment C on P.id = C.post_id
       left outer join Post_Tag PT on PT.post_id = P.id
       left outer join Tag T on PT.tag_id = T.id
  where B.id = #{id}
</select>
```

```xml
<!-- 非常复杂的结果映射 -->
<resultMap id="detailedBlogResultMap" type="Blog">
  <constructor>
    <idArg column="blog_id" javaType="int"/>
  </constructor>
  <result property="title" column="blog_title"/>
  <association property="author" javaType="Author">
    <id property="id" column="author_id"/>
    <result property="username" column="author_username"/>
    <result property="password" column="author_password"/>
    <result property="email" column="author_email"/>
    <result property="bio" column="author_bio"/>
    <result property="favouriteSection" column="author_favourite_section"/>
  </association>
  <collection property="posts" ofType="Post">
    <id property="id" column="post_id"/>
    <result property="subject" column="post_subject"/>
    <association property="author" javaType="Author"/>
    <collection property="comments" ofType="Comment">
      <id property="id" column="comment_id"/>
    </collection>
    <collection property="tags" ofType="Tag" >
      <id property="id" column="tag_id"/>
    </collection>
    <discriminator javaType="int" column="draft">
      <case value="1" resultType="DraftPost"/>
    </discriminator>
  </collection>
</resultMap>
```

**小结**

​	1.关联-association 【多对一】

​	2.集合-collection 【一对多】

​	3.javaType & ofType

​		1.javaType用来指定实体类中属性的类型

​		2.ofType用来指定映射到List或者集合中的pojo类型，泛型中的约束类型！



**慢SQL   1000s   |   好的SQL   1s**

# 面试高频：

- MySql引擎
- InnoDB的底层原理
- 索引
- 索引优化



# 12.动态sql

​	==什么是动态SQL：动态SQL就是指根据不同的条件生成不同的SQL语句==

使用动态 SQL 并非一件易事，但借助可用于任何 SQL 映射语句中的强大的动态 SQL 语言，MyBatis 显著地提升了这一特性的易用性。

如果你之前用过 JSTL 或任何基于类 XML 语言的文本处理器，你对动态 SQL 元素可能会感觉似曾相识。在 MyBatis 之前的版本中，需要花时间了解大量的元素。借助功能强大的基于 OGNL 的表达式，MyBatis 3 替换了之前的大部分元素，大大精简了元素种类，现在要学习的元素种类比原来的一半还要少。

- if
- choose (when, otherwise)
- trim (where, set)
- foreach

## 搭建环境

```mysql
CREATE TABLE `blog`(
`id` VARCHAR(50) NOT NULL COMMENT '博客id',
`title` VARCHAR(100) NOT NULL COMMENT '博客标题',
`author` VARCHAR(30) NOT NULL COMMENT '博客作者',
`create_time` DATETIME NOT NULL COMMENT '创建时间',
`views` INT(30) NOT NULL COMMENT '浏览量'
)ENGINE=INNODB DEFAULT CHARSET=utf8
```

**小贴士@SuppressWarnings：**

```java
@SuppressWarnings("all") //抑制警告，消除黄色波浪线
```

## IF

```xml
<select id="queryBlogIF" parameterType="map" resultType="com.luo.pojo.Blog">
    select
    	*
    from blog
    where 1=1
    <if test="title != null">
        and title = #{title}
    </if>
    <if test="author != null">
        and author = #{author}
    </if>
</select>
```

## choose (when, otherwise)

有时候，我们不想使用所有的条件，而只是想从多个条件中**选择一个使用**。针对这种情况，MyBatis 提供了 choose 元素，它有点像 Java 中的 switch 语句。

```xml
<select id="queryBlogChoose" parameterType="map" resultType="com.luo.pojo.Blog">
    select
    	*
    from blog
    <where>
        <choose>
            <when test="title != null and title != ''">
                and title = #{title}
            </when>
            <when test="author != null">
                and author = #{author}
            </when>
            <otherwise>
                and views &gt; 2000
            </otherwise>
        </choose>
    </where>
</select>
```

## trim (where, set)

*where* 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句。而且，若子句的开头为 “AND” 或 “OR”，*where* 元素也会将它们去除。

```xml
<select id="queryBlogIF" parameterType="map" resultType="com.luo.pojo.Blog">
    select
    	*
    from blog
    <where>
        <if test="title != null">
            and title = #{title}
        </if>
        <if test="author != null">
            and author = #{author}
        </if>
    </where>
</select>
```

用于动态更新语句的类似解决方案叫做 *set*。*set* 元素可以用于动态包含需要更新的列，忽略其它不更新的列。*set* 元素会动态地在行首插入 SET 关键字，并会删掉额外的逗号

```xml
<update id="updateBlog" parameterType="map">
    update blog
    <set>
        <if test="title != null">
            title = #{title},
        </if>
        <if test="author != null">
            author = #{author}
        </if>
    </set>
    where id = #{id}
</update>
```

trim：

where标签 = 

```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
```

set标签 = 

```xml
<trim prefix="SET" suffixOverrides=",">
  ...
</trim>
```

trim标签作用下的SQL的insert语句

```xml
insert into study
<trim prefix="(" suffix=")" suffixOverrides="," >
    <if test="id != null" >
        id,
    </if>
    <if test="name != null" >
        name,
    </if>
</trim>
<trim prefix="values (" suffix=")" suffixOverrides="," >
    <if test="id != null" >
        #{id},
    </if>
</trim>
```



==所谓的动态SQL，本质还是SQL语句，只是我们可以在SQL层面，去执行一个逻辑代码==



## Foreach

```xml
select 
	* 
from user 
where 1=1 
  and 
  
<foreach item="id" collection="ids"
         open="(" separator="or" close=")" nullable="true">
    id = #{id}
</foreach>

  
  (id=1 or id=2 or id=3)
```

动态 SQL 的另一个常见使用场景是对集合进行遍历（尤其是在构建 IN 条件语句的时候）

*foreach* 元素的功能非常强大，它允许你指定一个集合，声明可以在元素体内使用的**集合项（item）**和**索引（index）变量**。它也允许你指定开头与结尾的字符串以及集合项迭代之间的分隔符。这个元素也不会错误地添加多余的分隔符，看它多智能！

**提示** 你可以将任何可迭代对象（如 List、Set 等）、Map 对象或者数组对象作为集合参数传递给 *foreach*。当使用可迭代对象或者数组时，index 是当前迭代的序号，item 的值是本次迭代获取到的元素。当使用 Map 对象（或者 Map.Entry 对象的集合）时，index 是键，item 是值。

<font color = "red">**以下是Map实例，List也可以，注意也要加@Param("xx"),parameterType="map"改为parameterType="list"**</font>

```xml
Map<String, Object> map = new HashMap<>();
map.put("idss",ids);
map.put("id1","1");
map.put("id2","2");
map.put("id3","3");
List<Blog> blogs = mapper.queryBlogForeach(map);
---------------------------------------------------------------------------------
//查询第1-2-3号记录的博客
List<Blog> queryBlogForeach(@Param("ids") Map<String, Object> map);
---------------------------------------------------------------------------------
<select id="queryBlogForeach" parameterType="map" resultType="com.luo.pojo.Blog">
    select
    	*
    from blog
    <where>
        <foreach collection="ids" item="id"
                 open="id in (" separator="," close=")">
            #{id}
        </foreach>
    </where>
</select>
```



## SQL片段

有的时候，我们可能会将一些功能的部分抽取出来，方便复用！

1.使用SQL标签抽取公共的部分

```xml
<sql id="if-title-author">
    <if test="title != null">
        and title = #{title}
    </if>
    <if test="author != null">
        and author = #{author}
    </if>
</sql>
```

2.在需要使用的地方使用include标签引用即可

```xml
<select id="queryBlogIF" parameterType="map" resultType="com.luo.pojo.Blog">
    select
    	*
    from blog
    <where>
        <include refid="if-title-author"/>
    </where>
</select>
```



注意事项：

- 最好基于单表来定义SQL片段！
- 不要存在where标签



==动态SQL就是在拼接SQL语句，我们只要保证SQL的正确性，按照SQL的格式，去排列组合就可以了==

建议：

- 先在Mysql中写出完整的SQL，再对应地去修改成为我们的动态SQL实现通用即可！

# 13.缓存

## 13.1、简介

```
查询 ： 连接数据库，耗资源！
	一次查询的结果，给他暂存在一个可以直接取到的地方！ --> 内存
	
我们再次查询相同数据的时候，直接走缓存，就不用走数据库了
```



1.什么是缓存【Cache】？

- 存在内存中的临时数据。
- 将用户经常查询的数据放在缓存中，用户去查询数据就不用从磁盘上(关系型数据库文件)查询，从缓存中查询，从而提高查询效率，解决了高并发系统的性能问题。

2.为什么使用缓存？

- 减少和数据库的交互次数，减少系统开销，提高系统效率。

3.什么样的数据能使用缓存？

- 经常查询并且不经常改变的数据。【可以使用缓存】

## 13.2、Mybatis缓存

- Mybatis包含一个非常强大的查询缓存特性，**它可以非常方便地定制和配置缓存**。缓存可以极大地提升查询效率。
- Mybatis系统中默认定义了两级缓存：**一级缓存**和**二级缓存**
  - 默认情况下，只有一级缓存开启。（SqlSession级别的缓存，也称为本地缓存）
  - 二级缓存需要手动开启和配置，他是基于namespace级别的缓存（Mapper接口层面）
  - 为例提高拓展性，Mybatis定义了缓存接口Cache。我们可以通过实现Cache接口来自定义二级缓存

## 13.3、一级缓存

- 一级缓存也叫做本地缓存：SqlSession
  - 与数据库同一次会话期间查询到的数据会放在本地缓存中。
  - 以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库

测试步骤：

​	1.开启日志！

```xml
<settings>
    <!--标准的日志工厂实现-->
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```

​	2.测试在一个SqlSession中查询两次记录

```java
@Test
public void test(){
    SqlSession sqlSession = MybatisUtil.getSqlSession();
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

    User user = userMapper.queryUserById(3);
    System.out.println("user = " + user);

    System.out.println("=========================");

    User user2 = userMapper.queryUserById(3);
    System.out.println("user2 = " + user2);

    System.out.println(user == user2);

    sqlSession.close();
}
```

​	3.查看日志输出

```mysql
Logging initialized using 'class org.apache.ibatis.logging.stdout.StdOutImpl' adapter.
Class not found: org.jboss.vfs.VFS
JBoss 6 VFS API is not available in this environment.
Class not found: org.jboss.vfs.VirtualFile
VFS implementation org.apache.ibatis.io.JBoss6VFS is not valid in this environment.
Using VFS adapter org.apache.ibatis.io.DefaultVFS
Find JAR URL: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Not a JAR: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Reader entry: User.class
Listing file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Find JAR URL: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo/User.class
Not a JAR: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo/User.class
Reader entry: ����   4 ]
Checking to see if class com.luo.pojo.User matches criteria [is assignable to Object]
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
Opening JDBC Connection
Created connection 797925218.
==>  Preparing: select * from user where id = ?
==> Parameters: 3(Integer)
<==    Columns: id, name, pwd
<==        Row: 3, 小雪, 200115
<==      Total: 1
user = User(id=3, name=小雪, pwd=200115)
=========================
user2 = User(id=3, name=小雪, pwd=200115)
true
Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@2f8f5f62]
Returned connection 797925218 to pool.

Process finished with exit code 0
```

缓存失效的情况：

​	1.查询不同的东西

​	2.增删改操作，可能会改变原来的数据，所以必定会刷新缓存！

```java
@Test
public void test(){
    SqlSession sqlSession = MybatisUtil.getSqlSession();
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

    User user = userMapper.queryUserById(3);
    System.out.println("user = " + user);

    //中间更新，一级缓存失效
    userMapper.updateUser(new User(1,"小罗","1234567"));

    System.out.println("=========================");

    User user2 = userMapper.queryUserById(3);
    System.out.println("user2 = " + user2);

    System.out.println(user == user2);

    sqlSession.close();
}
```

console：

```mysql
Logging initialized using 'class org.apache.ibatis.logging.stdout.StdOutImpl' adapter.
Class not found: org.jboss.vfs.VFS
JBoss 6 VFS API is not available in this environment.
Class not found: org.jboss.vfs.VirtualFile
VFS implementation org.apache.ibatis.io.JBoss6VFS is not valid in this environment.
Using VFS adapter org.apache.ibatis.io.DefaultVFS
Find JAR URL: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Not a JAR: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Reader entry: User.class
Listing file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Find JAR URL: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo/User.class
Not a JAR: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo/User.class
Reader entry: ����   4 ^	  G	  H	  I J
Checking to see if class com.luo.pojo.User matches criteria [is assignable to Object]
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
Opening JDBC Connection
Created connection 797925218.
==>  Preparing: select * from user where id = ?
==> Parameters: 3(Integer)
<==    Columns: id, name, pwd
<==        Row: 3, 小雪, 200115
<==      Total: 1
user = User(id=3, name=小雪, pwd=200115)
==>  Preparing: update user set name = ?, pwd = ? where id = ?
==> Parameters: 小罗(String), 1234567(String), 1(Integer)
<==    Updates: 1
=========================
==>  Preparing: select * from user where id = ?
==> Parameters: 3(Integer)
<==    Columns: id, name, pwd
<==        Row: 3, 小雪, 200115
<==      Total: 1
user2 = User(id=3, name=小雪, pwd=200115)
false
Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@2f8f5f62]
Returned connection 797925218 to pool.

Process finished with exit code 0
```

​	3.查询不同的Mapper.xml

​	4.手动清理缓存

```java
//手动清理缓存
sqlSession.clearCache();
```



**小结：**一级缓存默认是开启的，只在一次SqlSession中有效，也就是拿到连接到关闭连接这个区间段！



## 13.4、二级缓存

- 二级缓存也叫做全局缓存，一级缓存作用域太低了，所以诞生了二级缓存
- 基于namespace级别的缓存，一个名称空间对应一个二级缓存；
- 工作机制
  - 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中；
  - 如果当前会话关闭了，这个会话对应的一级缓存就没了；但是我们想要的是，**会话关闭了，一级缓存中的数据被保存到二级缓存中；**
  - 新的会话查询信息，就可以从二级缓存中获取内容；
  - 不同的mapper查出的数据会放在自己对应的缓存(map)中



步骤：

​	1.开启全局缓存（默认是开启的）

```xml
<settings>
    <!--显式地开启全局缓存-->
    <setting name="cacheEnabled" value="true"/>
</settings>
```

​	2.在要使用二级缓存的Mapper中开启

```xml
<!--在当前的Mapper.xml中使用二级缓存-->
<cache/>
```

​	也可以自定义参数（**不定义参数，实体类需要实现序列化**）

```xml
<cache
  eviction="FIFO"
  flushInterval="60000"
  size="512"
  readOnly="true"/>
```

​	3.测试

```java
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
```

```mysql
Logging initialized using 'class org.apache.ibatis.logging.stdout.StdOutImpl' adapter.
Class not found: org.jboss.vfs.VFS
JBoss 6 VFS API is not available in this environment.
Class not found: org.jboss.vfs.VirtualFile
VFS implementation org.apache.ibatis.io.JBoss6VFS is not valid in this environment.
Using VFS adapter org.apache.ibatis.io.DefaultVFS
Find JAR URL: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Not a JAR: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Reader entry: User.class
Listing file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo
Find JAR URL: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo/User.class
Not a JAR: file:/F:/workspace/IdeaProjects/mybatis-study/mybatis-09/target/classes/com/luo/pojo/User.class
Reader entry: ����   4 `	  H	  I	  J K
Checking to see if class com.luo.pojo.User matches criteria [is assignable to Object]
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
Cache Hit Ratio [com.luo.dao.UserMapper]: 0.0
Opening JDBC Connection
Created connection 1601292138.
==>  Preparing: select * from user where id = ?
==> Parameters: 3(Integer)
<==    Columns: id, name, pwd
<==        Row: 3, 小雪, 200115
<==      Total: 1
user = User(id=3, name=小雪, pwd=200115)
Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@5f71c76a]
Returned connection 1601292138 to pool.
As you are using functionality that deserializes object streams, it is recommended to define the JEP-290 serial filter. Please refer to https://docs.oracle.com/pls/topic/lookup?ctx=javase15&id=GUID-8296D8E8-2B93-4B9A-856E-0A65AF9B8C66
Cache Hit Ratio [com.luo.dao.UserMapper]: 0.5
user2 = User(id=3, name=小雪, pwd=200115)

Process finished with exit code 0
```



小结：

- 只要开启了二级缓存，在同一个Mapper下就有效
- 所有的数据都会先放在一级缓存中；
- 只有当会话提交，或者关闭的时候，才会提交到二级缓存中



## 13.6、自定义缓存-ehcache

```
Ehcache是一种广泛使用的开源Java分布式缓存。主要面向通用缓存
```

要在程序中使用，先要导包

```xml
<!-- https://mvnrepository.com/artifact/org.mybatis.caches/mybatis-ehcache -->
<dependency>
    <groupId>org.mybatis.caches</groupId>
    <artifactId>mybatis-ehcache</artifactId>
    <version>1.1.0</version>
</dependency>
```

在mapper中指定使用我们的ehcache缓存实现！

```xml
<!--使用ehcache-->
<cache type="org.mybatis.caches.ehcache.EhcacheCache"/>
```

ehcache.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">

    <diskStore path="./tmpdir/Tmp_EhCache"/>

    <defaultCache
            eternal="false"
            maxElementsInMemory="10000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="259200"
            memoryStoreEvictionPolicy="LRU"/>

    <cache
            name="cloud_user"
            eternal="false"
            maxElementsInMemory="5000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="1800"
            memoryStoreEvictionPolicy="LRU"/>
</ehcache>
```

