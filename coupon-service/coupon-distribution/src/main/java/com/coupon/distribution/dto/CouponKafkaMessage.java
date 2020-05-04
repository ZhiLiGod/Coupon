package com.coupon.distribution.dto;

import lombok.Data;

import java.util.List;

@Data
public class CouponKafkaMessage {

  /**{@link com.coupon.distribution.constant.CouponStatus}*/
  private Integer status;
  /**{@link com.coupon.distribution.entity.Coupon} id*/
  private List<Long> ids;

}
