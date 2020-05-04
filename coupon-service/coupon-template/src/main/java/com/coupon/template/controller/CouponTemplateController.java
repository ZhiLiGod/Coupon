package com.coupon.template.controller;

import com.coupon.template.dto.TemplateRequest;
import com.coupon.template.entity.CouponTemplate;
import com.coupon.template.service.IBuildTemplateService;
import com.coupon.template.service.ITemplateBaseService;
import com.couponcommon.dto.CouponTemplateSDK;
import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/template")
public class CouponTemplateController {

  @Autowired
  private IBuildTemplateService buildTemplateService;

  @Autowired
  private ITemplateBaseService templateBaseService;

  @PostMapping("/build")
  public ResponseEntity<CouponTemplate> buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
    return ResponseEntity.ok(buildTemplateService.buildTemplate(request));
  }

  @GetMapping("/{id}/info")
  public ResponseEntity<CouponTemplate> buildTemplateInfo(@PathVariable Long id) throws CouponException {
    return ResponseEntity.ok(templateBaseService.buildTemplateInfo(id));
  }

  /**
   * for third party usage
   * @return {@link CouponTemplateSDK}
   */
  @GetMapping("/sdk/all")
  public ResponseEntity<List<CouponTemplateSDK>> findAllUsableTemplate() {
    return ResponseEntity.ok(templateBaseService.findAllUsableTemplates());
  }

  @GetMapping("/sdk/infos")
  public ResponseEntity<Map<Long, CouponTemplateSDK>> findIds2TemplateSDK(@RequestParam Collection<Long> ids) {
    return ResponseEntity.ok(templateBaseService.findIds2TemplateSDK(ids));
  }

}
