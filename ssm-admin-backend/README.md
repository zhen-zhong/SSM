SSM-Admin-Backend | 后端管理系统基建项目

本项目是一个基于 SSM (Spring + Spring MVC + MyBatis) 架构的高性能后端管理系统。项目采用前后端分离设计，深度整合了 RBAC 权限模型、JWT 动态鉴权、AOP 异步日志以及 Swagger 3.0 交互式文档。

一、🚀 技术栈

核心框架：Spring Framework 5.3.31
    1. Web 框架：Spring MVC
    2. 持久层：MyBatis 3.5 + Druid 连接池
    3. 数据库：MySQL 8.0 (Docker 容器化部署)
    4. 安全鉴权：JWT (JSON Web Token) + Spring Interceptor
    5. 日志系统：Spring AOP + 自定义注解 (@Log)
    6. 接口文档：Swagger 3.0 (OpenAPI 3.0)
    7. 工具类：Lombok, Jackson, JJWT

二、📂 项目目录结构说明

ssm-admin-backend
├── src/main/java/com/admin
│   ├── common
│   │   ├── annotation     # 自定义注解：@Log
│   │   ├── aspect         # AOP切面：LogAspect (自动化记录操作日志)
│   │   ├── handler        # 全局异常处理器：GlobalExceptionHandler
│   │   └── Result.java    # 统一响应格式对象
│   ├── config             # 配置中心：SwaggerConfig, WebMvcConfig, AuthInterceptor
│   ├── controller         # RESTful 控制层：SysUserController
│   ├── entity             # 实体类：SysUser, SysLog, SysRole 等
│   ├── mapper             # MyBatis 接口：SysUserMapper, SysLogMapper
│   ├── service            # 业务逻辑接口及其实现类 (impl)
│   └── utils              # 工具类：JwtUtils (Token颁发与校验)
├── src/main/resources
│   ├── mapper             # MyBatis SQL XML 映射文件
│   ├── applicationContext.xml # Spring 核心配置 (连接池、事务、AOP开关)
│   ├── jdbc.properties    # 数据库敏感信息
│   └── spring-mvc.xml     # MVC配置 (扫描、拦截器、静态资源放行)
└── web.xml                # 项目启动入口与编码过滤配置

三、🔒 核心功能模块

1. 自动化操作日志 (AOP)
通过在 Controller 方法上标注 @Log(module="用户管理", action="删除")，系统会自动捕获：操作人、IP地址、请求方法、参数、执行时长等信息，并持久化到 sys_log 表。

2. JWT 登录鉴权链路
无状态化：后端不存储 Session，通过校验请求头中的 Authorization 字段实现身份识别。

拦截器：AuthInterceptor 负责拦截非登录请求，并从 Token 中解析出用户信息存入 Request 域供后续业务使用。

3. 全局异常处理
配置了 @RestControllerAdvice，自动捕获空指针、鉴权失败、数据库异常等，返回统一格式的 JSON 报文，避免泄露后端堆栈。

四、📡 RESTful 接口布局

业务模块	功能描述	请求方式	接口路径	鉴权
认证模块	用户登录获取 Token	POST	/user/login	🔓 公开
用户管理	注册新用户	POST	/user/register	🔓 公开
用户管理	获取当前用户信息	GET	/user/info	🔐 需Token
用户管理	获取所有用户列表	GET	/user/list	🔐 需Token
用户管理	修改账号状态 (启用/停用)	PUT	/user/status	🔐 需Token
用户管理	逻辑删除用户	DELETE	/user/{id}	🔐 需Token

五、🚦 常用开发命令
1. 编译与启动
清理并运行（推荐）：

Bash
mvn clean compile jetty:run
仅编译检查：

Bash
mvn compile
2. 接口调试 (cURL 示例)
Step 1: 登录获取 Token

curl -X POST http://localhost:8080/user/login \
     -H "Content-Type: application/json" \
     -d '{"username": "admin", "password": "你的密码"}'

Step 2: 携带 Token 访问信息
curl -X GET "http://localhost:8080/user/info?username=admin" \
     -H "Authorization: 你的TOKEN_STRING"

3. 在线文档访问
Swagger UI: http://localhost:8080/swagger-ui/index.html
API Docs (JSON): http://localhost:8080/v3/api-docs

六、⚠️ 常见坑点与注意事项
逻辑删除：系统不执行 DELETE 语句，而是通过修改 is_deleted = 1 来隐藏数据。
Lombok 编译报错：若提示找不到 set/get 方法，请检查 IDE 是否安装了 Lombok 插件。
时区设置：jdbc.url 中已配置 serverTimezone=Asia/Shanghai，确保日志时间与本地对齐