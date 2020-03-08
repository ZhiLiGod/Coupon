package com.couponzuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication// *** not springbootapplication
public class CouponZuulApplication {

  public static void main(String[] args) {
    SpringApplication.run(CouponZuulApplication.class, args);
  }

}
