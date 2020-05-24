package com.coupon.permission.service;

import com.coupon.permission.entity.Path;
import com.coupon.permission.repository.PathRepository;
import com.coupon.permission.sdk.dto.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PathService {

  @Autowired
  private PathRepository pathRepository;

  /**
   * <h2>Add new path into db</h2>
   * @param request {@link CreatePathRequest}
   * @return Path pk
   */
  public List<Long> createPath(CreatePathRequest request) {
    List<CreatePathRequest.PathInfo> pathInfos = request.getPathInfos();
    List<CreatePathRequest.PathInfo> validRequests = new ArrayList<>(request.getPathInfos().size());
    List<Path> currentPath = pathRepository.findAllByServiceName(pathInfos.get(0).getServiceName());

    if (!CollectionUtils.isEmpty(currentPath)) {
      for (CreatePathRequest.PathInfo pathInfo : pathInfos) {
        boolean isValid = true;
        for (Path path : currentPath) {
          if (path.getPathPattern().equals(pathInfo.getPathPattern()) &&
            path.getHttpMethod().equals(pathInfo.getHttpMethod())) {
            isValid = false;
            break;
          }
        }

        if (isValid) {
          validRequests.add(pathInfo);
        }
      }
    } else {
      validRequests = pathInfos;
    }

    List<Path> paths = new ArrayList<>(validRequests.size());
    // @formatter:off
    validRequests.forEach(p -> paths.add(new Path(
      p.getPathPattern(),
      p.getHttpMethod(),
      p.getPathName(),
      p.getServiceName(),
      p.getOpMode()
    )));
    // @formatter:on

    return pathRepository.saveAll(paths).stream().map(Path::getId).collect(Collectors.toList());
  }

}
