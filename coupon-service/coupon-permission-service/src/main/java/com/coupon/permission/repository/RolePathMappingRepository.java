package com.coupon.permission.repository;

import com.coupon.permission.entity.RolePathMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePathMappingRepository extends JpaRepository<RolePathMapping, Long> {

  RolePathMapping findByRoleIdAndPathId(Long roleId, Long pathId);

}
