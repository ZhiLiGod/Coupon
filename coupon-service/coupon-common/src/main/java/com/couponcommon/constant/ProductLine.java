package com.couponcommon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductLine {

  // @formatter:off
  PRODUCT_ONE("Product One", 1),
  PRODUCT_TWO("Product Two", 2);
  // @formatter:on

  private String description;

  private Integer code;

  public static ProductLine of(@NonNull Integer code) {
    // @formatter:off
    return Stream.of(values())
      .filter(c -> c.code.equals(code))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    // @formatter:on
  }

}
