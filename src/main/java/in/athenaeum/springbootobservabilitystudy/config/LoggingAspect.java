package in.athenaeum.springbootobservabilitystudy.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Aspect for logging method execution details based on @AppLog annotation.
 * Provides configurable logging with execution time tracking.
 */

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("@annotation(in.athenaeum.springbootobservabilitystudy.config.AppLog)")
    public void appLogAnnotatedMethods() {}

    @Around("appLogAnnotatedMethods()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        
        String methodName = String.format("%s#%s", method.getDeclaringClass().getName(), method.getName());
        AppLog appLogInstance = method.getAnnotation(AppLog.class);
        
        AppLogLevel logLevel = appLogInstance.level();

        long startTime = System.nanoTime();

        Object[] args = joinPoint.getArgs();
        
        if (args.length > 0 && appLogInstance.includeArgs()) {
            log(logLevel, "{} execution starts with args: {}", methodName, Arrays.toString(args));
        } else {
            log(logLevel, "{} execution starts", methodName);
        }

        try {
            Object value = joinPoint.proceed();

            long executionTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to ms
            
            if (appLogInstance.includeResult() && appLogInstance.includeExecutionTime()) {
                log(logLevel, "{} execution completes with result {} in {}ms", methodName, value, executionTime);
            } else if (appLogInstance.includeExecutionTime()) {
                log(logLevel, "{} execution completes in {}ms", methodName, executionTime);
            } else {
                log(logLevel, "{} execution completes", methodName);
            }
            
            return value;
        } catch (Exception e) {
            long executionTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to ms
            logger.error("{} execution error after {}ms", methodName, executionTime, e);
            throw e;
        }
    }


    private void log(AppLogLevel level, String message, Object... args) {
        switch (level) {
            case TRACE -> logger.trace(message, args);
            case DEBUG -> logger.debug(message, args);
            case INFO -> logger.info(message, args);
            case WARN -> logger.warn(message, args);
            case ERROR -> logger.error(message, args);
        }
    }
}
