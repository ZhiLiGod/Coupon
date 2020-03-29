package com.coupon.template.service;

import com.coupon.template.entity.CouponTemplate;
import com.couponcommon.dto.CouponTemplateSDK;
import com.couponcommon.exception.CouponException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * CRUD service
 */
public interface ITemplateBaseService {

  CouponTemplate buildTemplateInfo(Long id) throws CouponException;

  List<CouponTemplateSDK> findAllUsableTemplates();

  Map<Long, CouponTemplateSDK> findIds2TemplateSDK(Collection<Long> ids);

}
