package com.coupon.distribution.repository;

import com.coupon.distribution.constant.CouponStatus;
import com.coupon.distribution.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

  List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

}
