package com.coupon.distribution.service;

import com.coupon.distribution.entity.Coupon;
import com.couponcommon.exception.CouponException;

import java.util.List;

public interface IRedisService {

  List<Coupon> getCachedCoupons(Long userId, Integer status);

  // save empty coupon to cache so that avoid cache penetration
  void saveEmptyCouponList2Cache(Long userId, List<Integer> statuses);

  String tryToAcquireCouponCodeFromCache(Integer templateId);

  Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;

}
