package com.sonnvt.blog.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Configuration
public class Logging {
    private static final ThreadLocal<UUID> requestUUID = new ThreadLocal<>();

    @Pointcut("execution(* com.sonnvt.blog.api.*.*(..))")
    public void apiMethods() {}

    @Pointcut("execution(* com.sonnvt.blog.service.*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.sonnvt.blog.database.repository.implement.*.*(..))")
    public void databaseMethods() {}

    @Around("serviceMethods() || apiMethods() || databaseMethods()")
    public Object doAroundApi(ProceedingJoinPoint joinPoint) throws Throwable {
        if (requestUUID.get() == null) {
            requestUUID.set(UUID.randomUUID());
        }
        UUID uuid = requestUUID.get();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String packageName = joinPoint.getTarget().getClass().getPackage().getName();
        String[] packageParts = packageName.split("\\.");
        String rootPackage = String.join(".", Arrays.copyOfRange(packageParts, 0, 3)) + ".";
        String subPackage = packageName.replaceFirst("^" + rootPackage, "");

        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("[START {} {} {}] method: {} - input : {}", subPackage, className, uuid, methodName, Arrays.toString(args));
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("[ERROR {} {} {}] method: {} - exception: {}", subPackage, className, uuid, methodName, e.getMessage(), e);
            throw e;
        }
        log.info("[END {} {} {}] method: {} - input : {}", subPackage, className, uuid, methodName, result);
        requestUUID.remove();
        return result;
    }
}
