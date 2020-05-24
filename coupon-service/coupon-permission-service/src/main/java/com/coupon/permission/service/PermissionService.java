package com.coupon.permission.service;

import com.coupon.permission.constant.RoleEnum;
import com.coupon.permission.entity.Path;
import com.coupon.permission.entity.Role;
import com.coupon.permission.entity.RolePathMapping;
import com.coupon.permission.entity.UserRoleMapping;
import com.coupon.permission.repository.PathRepository;
import com.coupon.permission.repository.RolePathMappingRepository;
import com.coupon.permission.repository.RoleRepository;
import com.coupon.permission.repository.UserRoleMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PermissionService {

  @Autowired
  private PathRepository pathRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private RolePathMappingRepository rolePathMappingRepository;

  @Autowired
  private UserRoleMappingRepository userRoleMappingRepository;

  /**
   * <h2>User permission check</h2>
   * @param userId
   * @param uri controller uri
   * @param httpMethod get, post, ...
   * @return true / false
   */
  public boolean checkPermission(Long userId, String uri, String httpMethod) {
    UserRoleMapping userRoleMapping = userRoleMappingRepository.findByUserId(userId);
    if (null == userRoleMapping) {
      log.error("userid not exist in UserRoleMapping. {}", userId);
      return false;
    }

    Optional<Role> role = roleRepository.findById(userRoleMapping.getRoleId());
    if (!role.isPresent()) {
      log.error("roleId not exist in Role: {}", userRoleMapping.getRoleId());
      return false;
    }

    if (role.get().getRoleTag().equals(RoleEnum.SUPER_ADMIN.getRoleName())) {
      return true;
    }

    Path path = pathRepository.findByPathPatternAndHttpMethod(uri, httpMethod);
    if (null == path) {
      return true;
    }

    RolePathMapping rolePathMapping = rolePathMappingRepository
      .findByRoleIdAndPathId(role.get().getId(), path.getId());

    return rolePathMapping != null;
  }

}
