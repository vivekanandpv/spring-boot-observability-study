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
import java.lang.reflect.Parameter;

/**
 * Aspect for logging method execution details based on @AppLog annotation.
 * Provides configurable logging with execution time tracking.
 */

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final ReflectionBasedSanitizer sanitizer;

    public LoggingAspect(ReflectionBasedSanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }


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
            String sanitizedArgs = sanitizeMethodParameters(method, args);
            log(logLevel, "{} execution starts with args: [{}]", methodName, sanitizedArgs);
        } else {
            log(logLevel, "{} execution starts", methodName);
        }

        try {
            Object value = joinPoint.proceed();

            long executionTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to ms
            
            if (appLogInstance.includeResult() && appLogInstance.includeExecutionTime()) {
                String sanitizedResult = sanitizer.sanitizeObject(value);
                log(logLevel, "{} execution completes with result {} in {}ms",
                        methodName, sanitizedResult, executionTime);
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

    private String sanitizeMethodParameters(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            if (i > 0) result.append(", ");

            Parameter param = parameters[i];
            Object arg = args[i];

            if (param.isAnnotationPresent(Sensitive.class)) {
                Sensitive annotation = param.getAnnotation(Sensitive.class);
                result.append(param.getName()).append("=").append(annotation.mask());
            } else {
                result.append(param.getName()).append("=").append(sanitizeValue(arg));
            }
        }

        return result.toString();
    }

    private String sanitizeValue(Object value) {
        if (value == null) return "null";
        return value.toString();
    }
}
