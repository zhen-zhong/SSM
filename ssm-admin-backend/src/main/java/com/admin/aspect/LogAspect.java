package com.admin.common.aspect;

import com.admin.common.annotation.Log;
import com.admin.entity.SysLog;
import com.admin.mapper.SysLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogMapper logMapper;

    @Around("@annotation(com.admin.common.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行目标方法
        Object result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        // 保存日志
        saveSysLog(point, time);
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();

        // 获取注解上的模块和动作
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            sysLog.setModule(logAnnotation.module());
            sysLog.setAction(logAnnotation.action());
        }

        // 获取请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = new ObjectMapper().writeValueAsString(args[0]);
            sysLog.setParams(params.length() > 500 ? params.substring(0, 500) : params);
        } catch (Exception e) {
            sysLog.setParams("解析参数失败");
        }

        // 获取请求信息 (IP, 当前用户)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            sysLog.setIp(request.getRemoteAddr());
            // 从拦截器存入的 request 属性中获取用户名
            sysLog.setUsername((String) request.getAttribute("currentUsername"));
        }

        sysLog.setExecuteTime(time);
        // 插入数据库
        logMapper.insert(sysLog);
    }
}