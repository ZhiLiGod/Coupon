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
@Table(name = "coupon_path")
public class Path implements Serializable {

  private static final long serialVersionUID = -3754633346765684078L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "path_pattern", nullable = false)
  private String pathPattern;

  @Column(name = "http_method", nullable = false)
  private String httpMethod;

  @Column(name = "path_name", nullable = false)
  private String pathName;

  @Column(name = "service_name", nullable = false)
  private String serviceName;

  @Column(name = "op_mode", nullable = false)
  private String opMode;

  /**
   * <h2>no pk constructor<h2/>
   * @param pathPattern
   * @param httpMethod
   * @param pathName
   * @param serviceName
   * @param opMode
   */
  public Path(String pathPattern, String httpMethod, String pathName, String serviceName, String opMode) {
    this.pathPattern = pathPattern;
    this.pathName = pathName;
    this.httpMethod = httpMethod;
    this.serviceName = serviceName;
    this.opMode = opMode;
  }

}
