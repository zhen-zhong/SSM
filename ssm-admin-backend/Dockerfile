FROM tomcat:9.0-jre17-temurin-jammy

ENV TZ=Asia/Shanghai

WORKDIR /usr/local/tomcat

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone \
    && rm -rf /usr/local/tomcat/webapps/*

COPY target/ssm-admin-backend.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]