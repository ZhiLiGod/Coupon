package com.coupon.distribution.feign.hystrix;

import com.coupon.distribution.feign.SettlementClient;
import com.couponcommon.dto.CommonResponse;
import com.couponcommon.dto.SettlementInfo;
import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

  private static final BigDecimal INVALID_COST = BigDecimal.valueOf(-1);

  @Override
  public CommonResponse<SettlementInfo> processRule(SettlementInfo settlementInfo) throws CouponException {
    log.error("[eureka-client-coupon-settlement] processRule request error");

    settlementInfo.setEmploy(Boolean.FALSE);
    settlementInfo.setCost(INVALID_COST);

    return new CommonResponse<>(-1,
      "[eureka-client-coupon-settlement] processRule request error",
      settlementInfo);
  }

}
