package com.couponcommon.dto;

import com.couponcommon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {

  private Expiration expiration;
  private Discount discount;
  // maximum coupon per ppl
  private Integer limitation;
  private Usage usage;
  // can be used with other coupon, same type of coupon is not allowed
  private String weight;

  public boolean validate() {
    // @formatter:off
    return expiration.validate()
      && StringUtils.isNotEmpty(weight)
      && discount.validate()
      && limitation > 0
      && usage.validate();
    // @formatter:on
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Expiration {
    // map to periodType.code
    private Integer period;
    private Integer gap;
    private Long deadline;

    boolean validate() {
      return null != PeriodType.of(period) && gap > 0 && deadline > 0;
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Discount {
    private Integer quota;
    private Integer base;

    boolean validate() {
      return quota > 0 && base > 0;
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Usage {
    private String province;
    private String city;
    private String goodsType;

    boolean validate() {
      // @formatter:off
      return StringUtils.isNotEmpty(province) &&
        StringUtils.isNotEmpty(city) &&
        StringUtils.isNotEmpty(goodsType);
      // @formatter:on
    }
  }

}
