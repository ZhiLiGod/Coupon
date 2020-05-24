package com.coupon.permission.detector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>Define controller permission</h1>
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CouponPermission {

  String description() default "";

  boolean readOnly() default true;

  /**
   * <h2>extra info</h2>
   * would be good to use json
   * @return
   */
  String extra() default "";

}
