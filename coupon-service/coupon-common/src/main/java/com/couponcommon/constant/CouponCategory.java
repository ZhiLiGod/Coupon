package com.couponcommon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponCategory {

  // @formatter:off
  FINAL_REDUCTION("Final Reduction", "001"),
  DISCOUNT("Discount", "002"),
  IMMEDIATE_REDUCTION("Immediate Reduction", "003");
  // @formatter:on

  private String description;

  private String code;

  public static CouponCategory of(@NonNull String code) {
    // @formatter:off
    return Stream.of(values())
      .filter(c -> c.code.equals(code))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    // @formatter:on
  }

}
