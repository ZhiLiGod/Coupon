package com.coupon.template.service;

import com.coupon.template.dto.TemplateRequest;
import com.coupon.template.entity.CouponTemplate;
import com.couponcommon.exception.CouponException;

public interface IBuildTemplateService {

  CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;

}
