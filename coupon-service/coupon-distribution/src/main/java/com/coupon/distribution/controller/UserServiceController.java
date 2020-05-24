package com.coupon.distribution.controller;

import com.coupon.distribution.dto.AcquireTemplateRequest;
import com.coupon.distribution.entity.Coupon;
import com.coupon.distribution.service.IUserService;
import com.couponcommon.dto.CouponTemplateSDK;
import com.couponcommon.dto.SettlementInfo;
import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserServiceController {

  @Autowired
  private IUserService userService;

  @GetMapping("/{userId}/coupons/{status}")
  public List<Coupon> findCouponsByStatus(@PathVariable Long userId, @PathVariable Integer status) throws CouponException {
    return userService.findCouponsByStatus(userId, status);
  }

  @GetMapping("/{userId}/available/templates")
  public List<CouponTemplateSDK> findAvailableTemplates(@PathVariable Long userId) throws CouponException {
    return userService.findAvailableTemplates(userId);
  }

  @PostMapping("/acquire/template")
  public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request) throws CouponException {
    return userService.acquireTemplate(request);
  }

  @PostMapping("/settlement")
  public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {
    return userService.settlement(info);
  }

}
