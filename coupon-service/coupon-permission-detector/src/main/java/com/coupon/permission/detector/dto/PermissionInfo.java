package com.coupon.permission.detector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionInfo {

  private String url;
  private String method;
  private boolean isReadOnly;
  private String description;
  private String extra;

  @Override
  public String toString() {
    return "PermissionInfo{" + "url='" + url + '\'' + ", method='" + method + '\'' + ", isReadOnly=" + isReadOnly + ", description='" + description + '\'' + '}';
  }
  
}
