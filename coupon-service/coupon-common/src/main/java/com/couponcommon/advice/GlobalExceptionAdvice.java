package com.couponcommon.advice;

import com.couponcommon.dto.CommonResponse;
import com.couponcommon.exception.CouponException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionAdvice {

  @ExceptionHandler(value = CouponException.class)
  public CommonResponse<String> couponExceptionHandler(HttpServletRequest request, CouponException ex) {
    CommonResponse<String> response = new CommonResponse<>(-1, "business error");
    response.setData(ex.getMessage());
    return response;
  }

}
