package com.couponzuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class CouponZuulApplication {

  public static void main(String[] args) {
    SpringApplication.run(CouponZuulApplication.class, args);
  }

}
