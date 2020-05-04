package com.coupon.distribution.feign;

import com.coupon.distribution.feign.hystrix.TemplateClientHystrix;
import com.couponcommon.dto.CommonResponse;
import com.couponcommon.dto.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(value = "eureka-client-coupon-template", fallback = TemplateClientHystrix.class)
public interface TemplateClient {

  @RequestMapping(value = "/coupon-template/template/sdk/all", method = RequestMethod.GET)
  CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

  @RequestMapping(value = "/coupon-template/template/sdk/infos", method = RequestMethod.GET)
  CommonResponse<Map<Long, CouponTemplateSDK>> findIds2TemplateSDK(@RequestParam Collection<Long> ids);

}
