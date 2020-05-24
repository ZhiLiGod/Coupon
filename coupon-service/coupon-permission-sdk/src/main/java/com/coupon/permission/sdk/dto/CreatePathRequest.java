package com.coupon.permission.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePathRequest {

  private List<PathInfo> pathInfos;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  private static class PathInfo {

    private String pathPattern;
    private String httpMethod;
    private String pathName;
    private String serviceName;
    private String opMode;

  }

}
