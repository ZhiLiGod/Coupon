package com.coupon.template.service.impl;

import com.coupon.template.entity.CouponTemplate;
import com.coupon.template.repository.CouponTemplateRepository;
import com.coupon.template.service.ITemplateBaseService;
import com.couponcommon.dto.CouponTemplateSDK;
import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {

  @Autowired
  private CouponTemplateRepository couponTemplateRepository;

  @Override
  public CouponTemplate buildTemplateInfo(Long id) throws CouponException {
    return couponTemplateRepository.findById(id)
      .orElseThrow(() -> new CouponException(String.format("Template is not exist with Id: %d", id)));
  }

  @Override
  public List<CouponTemplateSDK> findAllUsableTemplates() {
    List<CouponTemplate> templates = couponTemplateRepository.findAllByAvailableAndExpired(Boolean.TRUE, Boolean.FALSE);
    return templates.stream().map(this::template2SDK).collect(Collectors.toList());
  }

  @Override
  public Map<Long, CouponTemplateSDK> findIds2TemplateSDK(Collection<Long> ids) {
    List<CouponTemplate> templates = couponTemplateRepository.findAllById(ids);
    return templates.stream().map(this::template2SDK)
      .collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
  }

  // mapping
  private CouponTemplateSDK template2SDK(CouponTemplate template) {
    // @formatter:off
    return new CouponTemplateSDK(
      template.getId(),
      template.getName(),
      template.getLogo(),
      template.getDesc(),
      template.getCategory().getCode(),
      template.getProductLine().getCode(),
      template.getKey(),// not fully built yet
      template.getTarget().getCode(),
      template.getRule()
    );
    // @formatter:on
  }

}
