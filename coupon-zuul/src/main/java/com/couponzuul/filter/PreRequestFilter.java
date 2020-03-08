package com.couponzuul.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter {

  @Override
  protected Object customRun() {
    long start = System.currentTimeMillis();
    context.set("startTime", System.currentTimeMillis());
    return success();
  }

  @Override
  public int filterOrder() {
    return 0;
  }

}
