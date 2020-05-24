package com.couponzuul;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableZuulProxy
@SpringCloudApplication// *** not springbootapplication
public class CouponZuulApplication {

  public static void main(String[] args) {
    SpringApplication.run(CouponZuulApplication.class, args);
  }

}
