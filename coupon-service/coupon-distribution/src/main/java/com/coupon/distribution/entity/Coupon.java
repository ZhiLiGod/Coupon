package com.coupon.distribution.entity;

import com.coupon.distribution.constant.CouponStatus;
import com.coupon.distribution.converter.CouponStatusConverter;
import com.couponcommon.dto.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon")
@EntityListeners(AuditingEntityListener.class)
public class Coupon implements Serializable {

  private static final long serialVersionUID = -8112573212067104150L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "template_id", nullable = false)
  private Long templateId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "coupon_code", nullable = false)
  private String couponCode;

  @CreatedDate
  @Column(name = "assign_time", nullable = false)
  private LocalDateTime assignTime;

  @Convert(converter = CouponStatusConverter.class)
  @Column(name = "status", nullable = false)
  private CouponStatus status;

  @Transient
  private CouponTemplateSDK templateSDK;

  public static Coupon invalidCoupon() {
    Coupon coupon = new Coupon();
    coupon.setId(-1L);
    return coupon;
  }

  public Coupon(Long templateId, Long userId, String couponCode, CouponStatus status) {
    this.templateId = templateId;
    this.userId = userId;
    this.couponCode = couponCode;
    this.status = status;
  }

}
