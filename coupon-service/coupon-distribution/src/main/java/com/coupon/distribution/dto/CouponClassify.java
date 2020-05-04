package com.coupon.distribution.dto;

import com.coupon.distribution.constant.CouponStatus;
import com.coupon.distribution.entity.Coupon;
import com.couponcommon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>User Coupon Category</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponClassify {

  private List<Coupon> usable;
  private List<Coupon> used;
  private List<Coupon> expired;

  public static CouponClassify classify(List<Coupon> coupons) {
    List<Coupon> usable = new ArrayList<>(coupons.size());
    List<Coupon> used = new ArrayList<>(coupons.size());
    List<Coupon> expired = new ArrayList<>(coupons.size());

    coupons.forEach(c -> {
      boolean isTimeExpired;
      long currentTime = Timestamp.valueOf(LocalDateTime.now()).getTime();

      if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(PeriodType.REGULAR.getCode())) {
        isTimeExpired = c.getTemplateSDK().getRule().getExpiration().getDeadline() <= currentTime;
      } else {
        isTimeExpired = Timestamp.valueOf(c.getAssignTime()
          .plusDays(Long.valueOf(c.getTemplateSDK().getRule().getExpiration().getGap()))).getTime() <= currentTime;
      }

      if (CouponStatus.USED.equals(c.getStatus())) {
        used.add(c);
      } else if (CouponStatus.EXPIRED.equals(c.getStatus()) || isTimeExpired) {
        expired.add(c);
      } else {
        usable.add(c);
      }
    });

    return new CouponClassify(usable, used, expired);
  }

}
