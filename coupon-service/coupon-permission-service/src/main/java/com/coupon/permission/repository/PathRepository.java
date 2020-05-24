package com.coupon.permission.repository;

import com.coupon.permission.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PathRepository extends JpaRepository<Path, Long> {

  List<Path> findAllByServiceName(String serviceName);

  Path findByPathPatternAndHttpMethod(String pathPattern, String httpMethod);

}
