package in.athenaeum.springbootobservabilitystudy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class SpringBootObservabilityStudyApplication {
    private static Logger logger = LoggerFactory.getLogger(SpringBootObservabilityStudyApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootObservabilityStudyApplication.class, args);
        System.out.printf("Current logger implementation: %s", logger.getClass().getName());
    }

}
