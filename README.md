# Flash外卖

#### 介绍
一个简单的外卖软件，分为前台与后台两个管理模块，在后端可以实现对于菜品的添加，对员工的管理以及对于套餐的分类功能等。前端用户可以更改自己的个人信息，购买想要的菜品。

#### 软件架构
前端：HTML + Vue + Axios + ElementUI
后端：SpringBoot + SSM + MP

#### 使用说明
1. 创建数据库并执行flash.sql脚本，若数据库端口号非3306则修改application.yml中的配置
2. 修改application.yml中password为MySQL密码
分为前台和后台，登录网址分别为
http://localhost:8080/backend/index.html
http://localhost:8080/front/page/login.html

#### 亮点
1. 为集中处理系统异常，自定义统一的错误码，并封装了 全局异常处理器 ，屏蔽了项目冗余的报错细节、便于接口调用方理解和统一处理。
2. 基于静态 ThreadLocal 封装了线程隔离的全局上下文对象，便于在请求内部存取用户信息，减少用户远程查询次数。
3. 自定义 MyBatis Plus 的 MetaObjectHandler，配合全局上下文实现写数据前的创建时间、用户 id 字段的自动填充。
4. 为解决原生 Jdk 序列化器导致的缓存 key 值乱码问题，自定义 RedisTemplate Bean 的 Redis Key 序列化器为StringRedisSerializer。
5. 为省去重复编写用户校验的麻烦，基于 WebFilter 实现全局登录校验；并通过 AntPathMatcher 来匹配动态请求路径，实现灵活的可选鉴权。
6. 提高信息页加载速度，基于 Spring Cache 注解 + Redis 实现对信息的自动缓存，大幅降低数据库压力的同时降低接口响应耗时

