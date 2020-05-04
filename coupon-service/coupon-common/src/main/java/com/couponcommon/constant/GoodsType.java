package com.couponcommon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum GoodsType {

  ENTERTAINMENT("Entertainment", 1),
  SEAFOOD("Seafood", 2),
  FURNITURE("Furniture", 3),
  OTHERS("Others", 4),
  ALL("All", 5);

  private String desc;
  private Integer code;

  public static GoodsType of(@NonNull Integer code) {
    return Stream.of(GoodsType.values()).filter(g -> g.getCode().equals(code))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid code %d", code)));
  }

}
