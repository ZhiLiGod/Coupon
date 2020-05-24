package com.coupon.permission.controller;

import com.coupon.permission.sdk.dto.CheckPermissionRequest;
import com.coupon.permission.sdk.dto.CreatePathRequest;
import com.coupon.permission.service.PathService;
import com.coupon.permission.service.PermissionService;
import com.couponcommon.annotation.IgnoreResponseAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PermissionController {

  @Autowired
  private PathService pathService;

  @Autowired
  private PermissionService permissionService;

  @PostMapping("/create/path")
  public List<Long> createPath(@RequestBody CreatePathRequest request) {
    return pathService.createPath(request);
  }

  @IgnoreResponseAdvice
  @PostMapping("/check/permission")
  public boolean checkPermission(@RequestBody CheckPermissionRequest request) {
    return permissionService.checkPermission(request.getUserId(), request.getUri(), request.getHttpMethod());
  }

}
