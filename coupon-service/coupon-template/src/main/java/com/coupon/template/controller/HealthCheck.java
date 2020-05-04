package com.coupon.template.controller;

import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class HealthCheck {

  @Autowired
  private DiscoveryClient discoveryClient;

  @Autowired
  @Qualifier("eurekaRegistration")
  private Registration registration;

  @GetMapping("/health")
  public String health() {
    log.debug("view health api");
    return "CouponTemplate Is Ok";
  }

  @GetMapping("/exception")
  public String exception() throws CouponException {
    log.debug("view exception api");
    throw new CouponException("CouponTemplate has Some Problems");
  }

  @GetMapping("/info")
  public List<Map<String, Object>> info() {
    // 2 minutes to get service info from eureka server
    List<ServiceInstance> instances = discoveryClient.getInstances(registration.getServiceId());
    List<Map<String, Object>> result = new ArrayList<>(instances.size());
    instances.forEach(i -> {
      Map<String, Object> info = new HashMap<>();
      info.put("serviceId", i.getServiceId());
      info.put("instanceId", i.getInstanceId());
      info.put("port", i.getPort());
      result.add(info);
    });

    return result;
  }

}
