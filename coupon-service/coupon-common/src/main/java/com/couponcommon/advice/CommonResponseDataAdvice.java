package com.couponcommon.advice;

import com.couponcommon.annotation.IgnoreResponseAdvice;
import com.couponcommon.dto.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

  /**
   * check if needs to handle the response
   */
  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {

    // if class has @IgnoreResponseAdvice, then return false;
    if (returnType.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
      return false;
    }

    // if method has @IgnoreResponseAdvice, then return false;
    return !returnType.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class);
    // handle response (do beforeBodyWrite)
  }

  @Override
  // @formatter:off
  public Object beforeBodyWrite(Object body,
      MethodParameter returnType, MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
  // @formatter:on

    // define the final response object
    CommonResponse<Object> commonResponse = new CommonResponse<>(0, "");

    // if body is null, then response doesnt need to set data
    if (null == body) {
      return commonResponse;
    } else if (body instanceof CommonResponse) {
      commonResponse = (CommonResponse<Object>) body;
    } else {
      commonResponse.setData(body);
    }
    return commonResponse;
  }

}
