package com.couponcommon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum DistributeTarget {

  SINGLE("For Single User", 1),
  MULTI("For Multiple Users", 2);

  private String description;

  private Integer code;

  public static DistributeTarget of(Integer code) {
    // @formatter:off
    return Stream.of(values())
      .filter(c -> c.code.equals(code))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    // @formatter:on
  }

}
