package com.coupon.permission.sdk.permission;

import com.coupon.permission.sdk.dto.CheckPermissionRequest;
import com.coupon.permission.sdk.dto.CreatePathRequest;
import com.couponcommon.dto.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * <h1>Permission Feign Client<h1/>
 */
@FeignClient(value = "eureka-client-coupon-permission")
public interface PermissionClient {

  @RequestMapping(value = "/coupon-permission/create/path", method = RequestMethod.POST)
  CommonResponse<List<Long>> createPath(@RequestBody CreatePathRequest request);

  @RequestMapping(value = "/coupon-permission/check/permission", method = RequestMethod.POST)
  boolean checkPermission(@RequestBody CheckPermissionRequest request);

}
