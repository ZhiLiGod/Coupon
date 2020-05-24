package com.coupon.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponKafkaMessage {

  /**{@link com.coupon.distribution.constant.CouponStatus}*/
  private Integer status;
  /**{@link com.coupon.distribution.entity.Coupon} id*/
  private List<Long> ids;

}
