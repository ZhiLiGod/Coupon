package com.coupon.permission.repository;

import com.coupon.permission.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Long> {

  UserRoleMapping findByUserId(Long userId);

}
