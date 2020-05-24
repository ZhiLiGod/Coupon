package com.couponzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public abstract class AbstractSecurityFilter extends ZuulFilter {

  @Override
  public String filterType() {
    return FilterConstants.PRE_TYPE;
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletResponse response = ctx.getResponse();

    // if response success then go ahead
    return response.getStatus() == 0 || response.getStatus() == HttpStatus.OK.value();
  }

  @Override
  public Object run() throws ZuulException {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest req = ctx.getRequest();
    HttpServletResponse resp = ctx.getResponse();

    Boolean result = null;

    try {
      result = interceptCheck(req, resp);
    } catch (Exception e) {
      log.error("filter {} throws exception {}, {}", this.getClass().getSimpleName(), req.getRequestURI(), e.getMessage());
    }

    log.info("filter finished");

    if (result == null) {
      log.debug("filter {} throws null {}", this.getClass().getSimpleName(), req.getRequestURI());
      ctx.setSendZuulResponse(false);
      ctx.setResponseStatusCode(getHttpStatus());
      return null;
    }

    if (!result) {
      try {
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(getHttpStatus());
        resp.setHeader("Content-type", "application/json");
        resp.getWriter().write(getErrorMsg());
        ctx.setResponse(resp);
      } catch (IOException e) {
        log.error("filter check request result is false");
      }
    }

    return null;
  }

  protected abstract Boolean interceptCheck(HttpServletRequest req, HttpServletResponse resp) throws Exception;

  protected abstract int getHttpStatus();

  protected abstract String getErrorMsg();

}
