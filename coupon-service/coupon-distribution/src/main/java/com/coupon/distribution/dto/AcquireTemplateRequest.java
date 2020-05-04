package com.coupon.distribution.dto;

import com.couponcommon.dto.CouponTemplateSDK;
import lombok.Data;

@Data
public class AcquireTemplateRequest {

  private Long userId;
  private CouponTemplateSDK templateSDK;

}
