package com.coupon.distribution.service;

import com.coupon.distribution.dto.AcquireTemplateRequest;
import com.coupon.distribution.entity.Coupon;
import com.couponcommon.dto.CouponTemplateSDK;
import com.couponcommon.exception.CouponException;

import java.util.List;

public interface IUserService {

  List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

  List<CouponTemplateSDK> findAvailableTemplates(Long userId) throws CouponException;

  Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

}
