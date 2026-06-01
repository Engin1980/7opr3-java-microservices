package cz.osu.prf.kip.applogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
@ComponentScan(
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class AppLogServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AppLogServiceApplication.class, args);
  }

}
