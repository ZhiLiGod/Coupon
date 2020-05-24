package com.coupon.permission.detector;

import com.coupon.permission.detector.dto.PermissionInfo;
import com.coupon.permission.sdk.permission.PermissionClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * Permission Detect Listener
 * Run automatically after server ready
 */
@Slf4j
@Component
public class PermissionDetectorListener implements ApplicationListener<ApplicationReadyEvent> {

  private static final String KEY_SERVER_CTX = "server.servlet.context-path";
  private static final String KEY_SERVICE_NAME = "spring.application.name";

  @SuppressWarnings("all")
  @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    ApplicationContext ctx = applicationReadyEvent.getApplicationContext();

    // Use a new thread to free main thread
    new Thread(() -> {
      List<PermissionInfo> infoList = scanPermission(ctx);
      registerPermission(infoList, ctx);
    }).start();
  }

  /**
   * <h2>Register permission</h2>
   * @param permissionInfoList
   * @param ctx
   */
  private void registerPermission(List<PermissionInfo> permissionInfoList, ApplicationContext ctx) {
    PermissionClient permissionClient = ctx.getBean(PermissionClient.class);
    if (null == permissionClient) {
      log.error("No permission feign client");
      return;
    }

    String serviceName = ctx.getEnvironment().getProperty(KEY_SERVICE_NAME);
    log.info("serviceName: {}", serviceName);

    boolean result = new PermissionRegistry(permissionClient, serviceName).register(permissionInfoList);
    if (result) {
      log.info("*************** done register ***************");
    }
  }

  /**
   * <h2>Scan controller permission info in services</h2>
   * @param ctx
   * @return
   */
  private List<PermissionInfo> scanPermission(ApplicationContext ctx) {
    String pathPrefix = ctx.getEnvironment().getProperty(KEY_SERVER_CTX);

    // get spring mapping bean
    RequestMappingHandlerMapping mappingBean = (RequestMappingHandlerMapping) ctx.getBean("requestMappingHandlerMapping");

    List<PermissionInfo> permissionInfoList = new AnnotationScanner(pathPrefix).scanPermission(
      mappingBean.getHandlerMethods()
    );

    permissionInfoList.forEach(p -> log.info("{}", p));
    log.info("{} permission found", permissionInfoList.size());
    log.info("*************** done scanning ***************");
    return permissionInfoList;
  }

}
