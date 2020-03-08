package com.couponzuul.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {

  @Override
  protected Object customRun() {
    HttpServletRequest request = context.getRequest();
    long startTime = (long) context.get("startTime");
    String uri = request.getRequestURI();
    long currentTime = System.currentTimeMillis();
    log.info("Request: {}, Duration: {}", uri, currentTime - startTime);
    return success();
  }

  @Override
  public int filterOrder() {
    return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
  }

}
