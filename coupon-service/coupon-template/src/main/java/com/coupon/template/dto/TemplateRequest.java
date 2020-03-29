package com.coupon.template.dto;

import com.couponcommon.dto.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {

  @NotBlank
  private String name;
  @NotBlank
  private String logo;
  @NotBlank
  private String desc;
  @NotBlank
  private String category;
  @NotNull
  private Integer productLine;
  @NotNull
  private Integer count;
  @NotNull
  private Long userId;
  @NotNull
  private Integer target;
  private TemplateRule rule;

  public boolean validate() {
    return rule.validate();
  }

}
