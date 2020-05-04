package com.coupon.distribution.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponStatus {

  USABLE("usable", 1), USED("used", 2), EXPIRED("expired", 3);

  private String desc;
  private Integer code;

  public static CouponStatus of(@NonNull Integer code) {
    return Stream.of(CouponStatus.values()).filter(c -> c.code.equals(code))
      .findAny().orElseThrow(() -> new IllegalArgumentException("Invalid code"));
  }

}
