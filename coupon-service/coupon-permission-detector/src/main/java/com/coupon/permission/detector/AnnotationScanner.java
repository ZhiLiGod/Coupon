package com.coupon.permission.detector;

import com.coupon.permission.detector.annotation.CouponPermission;
import com.coupon.permission.detector.annotation.IgnorePermission;
import com.coupon.permission.detector.dto.PermissionInfo;
import com.coupon.permission.sdk.enums.HttpMethodEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <h1>Permission annotation scanner</h1>
 */
@Slf4j
public class AnnotationScanner {

  private String pathPrefix;
  private static final String COUPON_PKG = "com.coupon";

  AnnotationScanner(String pathPrefix) {
    this.pathPrefix = trimPath(pathPrefix);
  }

  /**
   * build all controllers' permission info
   * @param mappingMap
   * @return
   */
  List<PermissionInfo> scanPermission(Map<RequestMappingInfo, HandlerMethod> mappingMap) {
    List<PermissionInfo> result = new ArrayList<>();
    mappingMap.forEach((mapInfo, method) ->
      result.addAll(buildPermission(mapInfo, method))
    );

    return result;
  }

  /**
   * <h2>Build controller's permission info</h2>
   * @param mapInfo {@link RequestMappingInfo} map to @RequestMapping
   * @param handlerMethod {@link HandlerMethod} details of @RequestMapping
   * @return List<PermissionInfo>
   */
  private List<PermissionInfo> buildPermission(RequestMappingInfo mapInfo, HandlerMethod handlerMethod) {
    Method javaMethod = handlerMethod.getMethod();
    Class baseClass = javaMethod.getDeclaringClass();

    // ignore doesn't start with com.coupon
    if (!isCouponPackage(baseClass.getName())) {
      log.debug("Ignore method: {}", javaMethod.getName());
      return Collections.emptyList();
    }

    IgnorePermission ignorePermission = javaMethod.getAnnotation(IgnorePermission.class);
    if (null != ignorePermission) {
      log.debug("Ignore method: {}", javaMethod.getName());
      return Collections.emptyList();
    }

    CouponPermission couponPermission = javaMethod.getAnnotation(CouponPermission.class);
    if (null == couponPermission) {
      log.error("Lack @CouponPermission on {}, {}", javaMethod.getDeclaringClass().getName(), javaMethod.getName());
      return Collections.emptyList();
    }

    Set<String> urlSet = mapInfo.getPatternsCondition().getPatterns();
    boolean isAllMethods = false;
    Set<RequestMethod> methodSet = mapInfo.getMethodsCondition().getMethods();
    if (CollectionUtils.isEmpty(methodSet)) {
      isAllMethods = true;
    }

    List<PermissionInfo> infos = new ArrayList<>();

    for (String url : urlSet) {
      if (isAllMethods) {
        PermissionInfo info = buildPermissionInfo(HttpMethodEnum.ALL.name(),
          javaMethod.getName(),
          this.pathPrefix + url,
          couponPermission.readOnly(),
          couponPermission.description(),
          couponPermission.extra());
        infos.add(info);
        continue;
      }

      for (RequestMethod method : methodSet) {
        PermissionInfo info = buildPermissionInfo(method.name(),
          javaMethod.getName(),
          this.pathPrefix + url,
          couponPermission.readOnly(),
          couponPermission.description(),
          couponPermission.extra());
        infos.add(info);
      }
    }

    return infos;
  }

  private PermissionInfo buildPermissionInfo(String reqMethod, String javaMethod, String path, Boolean readOnly,
    String description, String extra) {

    PermissionInfo info = new PermissionInfo();
    info.setReadOnly(readOnly);
    info.setDescription(StringUtils.isEmpty(description) ? javaMethod : description);
    info.setExtra(extra);
    info.setMethod(reqMethod);
    info.setUrl(path);
    return info;
  }

  private boolean isCouponPackage(String className) {
    return className.startsWith(COUPON_PKG);
  }

  /**
   * <h2>ensure path starts with /, ends without /<h2/>
   * example: user -> /user, /user/ -> /user
   * @param path
   * @return
   */
  private String trimPath(String path) {
    if (StringUtils.isEmpty(path)) {
      return "";
    }

    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }

    return path;
  }

}
