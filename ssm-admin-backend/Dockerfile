# 第一阶段：Maven 编译打包
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
# 复制 pom.xml 和源码
COPY pom.xml .
COPY src ./src
# 执行 maven 打包，跳过测试
RUN mvn clean package -DskipTests

# 第二阶段：Tomcat 运行阶段 (必须用 Tomcat 9 以兼容 javax.servlet)
FROM tomcat:9.0-eclipse-temurin-17

# 【重要修改】设置容器时区为上海，确保 AOP 操作日志时间(new Date)与本地对齐
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 清理 Tomcat 默认的 webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# 将编译好的 war 包复制过来，并重命名为 ROOT.war (实现根路径访问)
COPY --from=builder /build/target/ssm-admin-backend.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
# 启动 Tomcat
CMD ["catalina.sh", "run"]