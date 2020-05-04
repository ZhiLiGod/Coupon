package com.coupon.distribution.feign.hystrix;

import com.coupon.distribution.feign.TemplateClient;
import com.couponcommon.dto.CommonResponse;
import com.couponcommon.dto.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

  @Override
  public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
    log.error("[eureka-client-coupon-template] findAllUsableTemplate request error");
    return new CommonResponse<>(-1,
      "[eureka-client-coupon-template] findAllUsableTemplate request error",
      Collections.emptyList());
  }

  @Override
  public CommonResponse<Map<Long, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Long> ids) {
    log.error("[eureka-client-coupon-template] findIds2TemplateSDK request error");
    return new CommonResponse<>(-1,
      "[eureka-client-coupon-template] findIds2TemplateSDK request error",
      Collections.emptyMap());
  }

}
