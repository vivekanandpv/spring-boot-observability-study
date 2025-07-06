package in.athenaeum.springbootobservabilitystudy.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppLog {
    AppLogLevel level() default AppLogLevel.DEBUG;
    boolean includeArgs() default false;
    boolean includeResult() default false;
    boolean includeExecutionTime() default true;
}
