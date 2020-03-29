package com.couponcommon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateSDK {

  private Long id;
  private String name;
  private String logo;
  private String desc;
  private String category;
  private Integer productLine;
  private String key;
  private Integer target;
  private TemplateRule rule;

}
