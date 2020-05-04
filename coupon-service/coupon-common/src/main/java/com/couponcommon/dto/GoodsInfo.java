package com.couponcommon.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsInfo {

  /** {@link com.couponcommon.constant.GoodsType} */
  private Integer type;
  private BigDecimal price;
  private Integer count;

}
