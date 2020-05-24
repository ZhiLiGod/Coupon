package com.coupon.permission.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon_role")
public class Role implements Serializable {

  private static final long serialVersionUID = -1791421792631636684L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "role_name", nullable = false)
  private String roleName;

  @Column(name = "role_tag", nullable = false)
  private String roleTag;

}
