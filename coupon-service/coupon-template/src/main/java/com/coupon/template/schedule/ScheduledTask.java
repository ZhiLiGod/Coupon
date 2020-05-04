package com.coupon.template.schedule;

import com.coupon.template.entity.CouponTemplate;
import com.coupon.template.repository.CouponTemplateRepository;
import com.couponcommon.dto.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ScheduledTask {

  @Autowired
  private CouponTemplateRepository couponTemplateRepository;

  // 1 hour
  @Scheduled(fixedRate = 60 * 60 * 1000)
  public void offlineCouponTemplate() {
    log.info("Start to Expire CouponTemplate");
    List<CouponTemplate> templates = couponTemplateRepository.findAllByExpired(Boolean.FALSE);

    if (CollectionUtils.isEmpty(templates)) {
      log.info("Done to Expire CouponTemplate");
      return;
    }

    LocalDateTime now = LocalDateTime.now();
    List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());
    templates.forEach(t -> {
      TemplateRule rule = t.getRule();
      if (rule.getExpiration().getDeadline() < Timestamp.valueOf(now).getTime()) {
        t.setExpired(Boolean.TRUE);
        expiredTemplates.add(t);
      }
    });

    if (!CollectionUtils.isEmpty(expiredTemplates)) {
      log.info("Expired Coupon Templates Num: {}", couponTemplateRepository.saveAll(expiredTemplates));
    }

    log.info("Done to Expire CouponTemplate");
  }

}
