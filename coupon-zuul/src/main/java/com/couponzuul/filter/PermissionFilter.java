package com.couponzuul.filter;

import com.alibaba.fastjson.JSON;
import com.coupon.permission.sdk.dto.CheckPermissionRequest;
import com.coupon.permission.sdk.permission.PermissionClient;
import com.couponcommon.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class PermissionFilter extends AbstractSecurityFilter {

  @Autowired
  private PermissionClient permissionClient;

  @Override
  protected Boolean interceptCheck(HttpServletRequest req, HttpServletResponse resp) throws Exception {

    // get userId, from header.. cookie.. session
    Long userId = Long.valueOf(req.getHeader("userId"));
    String uri = req.getRequestURI().substring("/coupon".length());
    String httpMethod = req.getMethod();

    return permissionClient.checkPermission(new CheckPermissionRequest(userId, uri, httpMethod));
  }

  @Override
  protected int getHttpStatus() {
    return HttpStatus.OK.value();
  }

  @Override
  protected String getErrorMsg() {
    CommonResponse<Object> response = new CommonResponse<>();
    response.setCode(-2);
    response.setMessage("No permission");
    return JSON.toJSONString(response);
  }

}
