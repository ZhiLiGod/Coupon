package com.coupon.distribution.feign;

import com.coupon.distribution.feign.hystrix.SettlementClientHystrix;
import com.couponcommon.dto.CommonResponse;
import com.couponcommon.dto.SettlementInfo;
import com.couponcommon.exception.CouponException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "eureka-client-coupon-settlement", fallback = SettlementClientHystrix.class)
public interface SettlementClient {

  @RequestMapping(value = "/coupon-settlement/settlement/process", method = RequestMethod.POST)
  CommonResponse<SettlementInfo> processRule(@RequestBody SettlementInfo settlementInfo) throws CouponException;

}
