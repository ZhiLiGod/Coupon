package com.coupon.template.repository;

import com.coupon.template.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {

  CouponTemplate findByName(String name);

  List<CouponTemplate> findAllByAvailableAndExpired(boolean available, boolean expired);

  List<CouponTemplate> findAllByExpired(boolean expired);

}
