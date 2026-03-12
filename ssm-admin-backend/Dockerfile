# 使用带有国内可用代理前缀的基础镜像，直接绕过 403 限制
FROM dockerpull.com/library/tomcat:9.0-eclipse-temurin-17

# 设置时区为上海，保证 AOP 日志时间准确
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 清理 Tomcat 默认 webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# 直接把 Jenkins 刚刚编译好的 war 包放进去作为根目录项目
COPY target/ssm-admin-backend.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]