package com.couponzuul.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@SuppressWarnings("all")
public class RateLimiterFilter extends AbstractPreZuulFilter {

  /**
   * 2 tokens per sec
   */
  RateLimiter rateLimiter = RateLimiter.create(2.0);

  @Override
  protected Object customRun() {
    HttpServletRequest request = context.getRequest();

    if (rateLimiter.tryAcquire()) {
      log.info("Get rate token success.");
      return success();
    } else {
      log.error("Rate limited: {}", request.getRequestURI());
      return fail(429, "Error rate limited");
    }
  }

  @Override
  public int filterOrder() {
    return 2;
  }

}
