package eighty4.learning.spring.native_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SpringNativeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringNativeApplication.class, args);
	}

}
