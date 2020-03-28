package com.couponcommon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

/**
 * 有效期类型
 */
@Getter
@AllArgsConstructor
public enum PeriodType {

  // @formatter:off
  REGULAR("Constant Date", 1),
  SHIFT("In-constant Date", 2);
  // @formatter:on

  private String description;

  private Integer code;

  public static PeriodType of(@NonNull Integer code) {
    // @formatter:off
    return Stream.of(values())
      .filter(c -> c.code.equals(code))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    // @formatter:on
  }

}
