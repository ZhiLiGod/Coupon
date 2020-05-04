package com.couponcommon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SettlementInfo {

  private Long userId;
  private List<GoodsInfo> goodsInfos;
  private List<CouponAndTemplateInfo> couponAndTemplateInfos;
  private BigDecimal cost;
  // calc or pay
  private boolean employ;

  @Data
  public static class CouponAndTemplateInfo {

    /**Coupon Id*/
    private Long id;
    private CouponTemplateSDK template;

  }

}
