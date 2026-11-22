package com.dentalclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Bật @Scheduled cho các job định kỳ (sao chép lịch tuần, nhắc tái khám, đánh dấu vắng mặt)
@EnableScheduling
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class PhongKhamApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhongKhamApplication.class, args);
    }
}
