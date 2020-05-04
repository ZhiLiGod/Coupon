package com.coupon.template.service.impl;

import com.coupon.template.dto.TemplateRequest;
import com.coupon.template.entity.CouponTemplate;
import com.coupon.template.repository.CouponTemplateRepository;
import com.coupon.template.service.IAsyncService;
import com.coupon.template.service.IBuildTemplateService;
import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

  @Autowired
  private IAsyncService iAsyncService;

  @Autowired
  private CouponTemplateRepository couponTemplateRepository;

  @Override
  public CouponTemplate buildTemplate(TemplateRequest request)
    throws CouponException {

    // validation
    if (!request.validate()) {
      throw new CouponException("BuildTemplate Param Is Not Valid");
    }

    if (null != couponTemplateRepository.findByName(request.getName())) {
      throw new CouponException("Exist Same Name Template");
    }

    // build coupon template and save into database
    CouponTemplate template = requestToTemplate(request);
    template = couponTemplateRepository.save(template);

    // create template by using async service
    iAsyncService.asyncConstructCouponByTemplate(template);
    return template;
  }

  // mapping
  private CouponTemplate requestToTemplate(TemplateRequest request) {
    // @formatter:off
    return new CouponTemplate(
      request.getName(),
      request.getLogo(),
      request.getDesc(),
      request.getCategory(),
      request.getProductLine(),
      request.getCount(),
      request.getUserId(),
      request.getTarget(),
      request.getRule()
    );
    // @formatter:on
  }

}
