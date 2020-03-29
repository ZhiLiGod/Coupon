package com.coupon.template.entity;

import com.couponcommon.constant.CouponCategory;
import com.couponcommon.constant.DistributeTarget;
import com.couponcommon.constant.ProductLine;
import com.couponcommon.dto.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupon_template")
@EntityListeners(AuditingEntityListener.class)
public class CouponTemplate implements Serializable {

  private static final long serialVersionUID = -6988070358485620431L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "available", nullable = false)
  private Boolean available;

  @Column(name = "expired", nullable = false)
  private Boolean expired;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "logo", nullable = false)
  private String logo;

  @Column(name = "intro", nullable = false)
  private String desc;

  @Column(name = "category", nullable = false)
  private CouponCategory category;

  @Column(name = "product_line", nullable = false)
  private ProductLine productLine;

  @Column(name = "coupon_count", nullable = false)
  private Integer count;

  @CreatedDate
  @Column(name = "create_time", nullable = false)
  private LocalDateTime createTime;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "template_key", nullable = false)
  private String key;

  @Column(name = "target", nullable = false)
  private DistributeTarget target;

  @Column(name = "rule", nullable = false)
  private TemplateRule rule;

  // @formatter:off
  public CouponTemplate(String name, String logo, String desc, String category,
    Integer productLine, Integer count, Long userId, Integer target, TemplateRule rule) {

    this.available = Boolean.FALSE;
    this.expired = Boolean.FALSE;
    this.name = name;
    this.logo = logo;
    this.desc = desc;
    this.category = CouponCategory.of(category);
    this.productLine = ProductLine.of(productLine);
    this.count = count;
    this.userId = userId;
    // key = 4(product line and type) + 8(date: 20200101) + id(4)
    this.key = productLine.toString() + category +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    this.target = DistributeTarget.of(target);
    this.rule = rule;
  }
  // @formatter:on
}
