package com.coupon.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@EnableEurekaClient
@SpringBootApplication
public class CouponTemplateApplication {

  public static void main(String[] args) {
    SpringApplication.run(CouponTemplateApplication.class, args);
  }

}
