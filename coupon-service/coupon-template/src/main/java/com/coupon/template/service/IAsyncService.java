package com.coupon.template.service;

import com.coupon.template.entity.CouponTemplate;

public interface IAsyncService {

  /**
   * async creating coupon
   * @param template
   */
  void asyncConstructCouponByTemplate(CouponTemplate template);

}
