package com.admin.config;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 🌟 如果使用纯注解替代了 applicationContext.xml，必须指定 mapper.xml 的路径
        // 假设你的 XML 放在 src/main/resources/mapper/ 下
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml"));

        // 分页插件配置
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("reasonable", "true"); 
        properties.setProperty("supportMethodsArguments", "true");
        pageInterceptor.setProperties(properties);

        // 🌟 将拦截器添加到 MyBatis 插件链中，分页在此刻真正生效！
        factoryBean.setPlugins(new Interceptor[]{pageInterceptor});

        return factoryBean.getObject();
    }
}