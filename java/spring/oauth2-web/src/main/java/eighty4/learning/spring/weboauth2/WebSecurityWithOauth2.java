package eighty4.learning.spring.weboauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class WebSecurityWithOauth2 {

    public static void main(String[] args) {
        SpringApplication.run(WebSecurityWithOauth2.class, args);
    }

    @Configuration
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .oauth2Login();
        }
    }

    @Controller
    public static class MySecureWebpage {

        @GetMapping("/secured")
        public ResponseEntity<String> youCannotGetToThisWithoutAuthing() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<>("<!DOCTYPE html>", headers, HttpStatus.OK);
        }
    }
}
