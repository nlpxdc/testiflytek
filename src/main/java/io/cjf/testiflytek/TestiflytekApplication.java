package io.cjf.testiflytek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TestiflytekApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestiflytekApplication.class, args);
    }

}
