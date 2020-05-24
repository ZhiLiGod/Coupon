package com.coupon.distribution.controller;

import com.couponcommon.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class RibbonController {

  @Autowired
  private RestTemplate restTemplate;

  @GetMapping("/info")
  @IgnoreResponseAdvice
  public TemplateInfo getTemplateInfo() {
    String url = "http://eureka-client-coupon-template/coupon-template/info";
    return restTemplate.getForEntity(url, TemplateInfo.class).getBody();
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class TemplateInfo {
    private Integer code;
    private String message;
    private List<Map<String, Object>> data;
  }

}
