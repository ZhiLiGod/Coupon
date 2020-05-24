package com.coupon.permission.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

  ADMIN("admin"), SUPER_ADMIN("super admin"), CUSTOM("custom");

  private String roleName;

}
