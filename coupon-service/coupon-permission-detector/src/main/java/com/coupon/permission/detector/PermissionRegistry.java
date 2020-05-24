package com.coupon.permission.detector;

import com.coupon.permission.detector.dto.PermissionInfo;
import com.coupon.permission.sdk.dto.CreatePathRequest;
import com.coupon.permission.sdk.enums.OpModeEnum;
import com.coupon.permission.sdk.permission.PermissionClient;
import com.couponcommon.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>Permission Registry</h1>
 */
@Slf4j
public class PermissionRegistry {

  /** feign interface*/
  private PermissionClient permissionClient;

  private String serviceName;

  PermissionRegistry(PermissionClient permissionClient, String serviceName) {
    this.permissionClient = permissionClient;
    this.serviceName = serviceName;
  }

  boolean register(List<PermissionInfo> infos) {
    if (CollectionUtils.isEmpty(infos)) {
      return false;
    }

    // @formatter:off
    List<CreatePathRequest.PathInfo> pathInfos = infos.stream()
      .map(info -> CreatePathRequest.PathInfo.builder()
      .pathPattern(info.getUrl())
      .httpMethod(info.getMethod())
      .pathName(info.getDescription())
      .serviceName(serviceName)
      .opMode(info.isReadOnly() ? OpModeEnum.READ.name() : OpModeEnum.WRITE.name())
      .build()).collect(Collectors.toList());
    // @formatter:on

    CommonResponse<List<Long>> response = permissionClient.createPath(
      new CreatePathRequest(pathInfos)
    );

    if (!CollectionUtils.isEmpty(response.getData())) {
      log.info("Registered path info: {}", response.getData());
      return true;
    }

    return false;
  }

}
