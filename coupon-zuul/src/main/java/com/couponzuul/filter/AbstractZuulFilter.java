package com.couponzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.MediaType;

public abstract class AbstractZuulFilter extends ZuulFilter {

  // Saved in ThreadLocal, so Thread Save
  RequestContext context;

  private final static String NEXT = "next";

  @Override
  public boolean shouldFilter() {
    RequestContext ctx = RequestContext.getCurrentContext();
    return (boolean) ctx.getOrDefault(NEXT, true);
  }

  @Override
  public Object run() throws ZuulException {
    context = RequestContext.getCurrentContext();
    return customRun();
  }

  protected abstract Object customRun();

  Object fail(int code, String msg) {
    context.set(NEXT, false);
    context.setSendZuulResponse(false);
    context.getResponse().setContentType(MediaType.TEXT_HTML_VALUE);
    context.setResponseStatusCode(code);
    // @formatter:off
    context.setResponseBody(String.format(
        "{\"result\": \"%s!\"}", msg
    ));
    // @formatter:on

    return null;
  }

  Object success() {
    context.set(NEXT, true);
    return null;
  }

}
