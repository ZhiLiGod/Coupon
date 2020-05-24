package com.coupon.distribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.coupon.distribution.constant.CouponStatus;
import com.coupon.distribution.dto.AcquireTemplateRequest;
import com.coupon.distribution.dto.CouponClassify;
import com.coupon.distribution.dto.CouponKafkaMessage;
import com.coupon.distribution.entity.Coupon;
import com.coupon.distribution.feign.SettlementClient;
import com.coupon.distribution.feign.TemplateClient;
import com.coupon.distribution.repository.CouponRepository;
import com.coupon.distribution.service.IRedisService;
import com.coupon.distribution.service.IUserService;
import com.couponcommon.constant.Constant;
import com.couponcommon.dto.CouponTemplateSDK;
import com.couponcommon.dto.SettlementInfo;
import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h1>User Service Implementation<h1/>
 * All status are saved in Redis and Kafka do persist from Kafka
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private IRedisService redisService;

  @Autowired
  private TemplateClient templateClient;

  @Autowired
  private SettlementClient settlementClient;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
    List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
    List<Coupon> preTarget;

    if (!CollectionUtils.isEmpty(curCached)) {
      log.debug("cached coupon is not empty: {} {}", userId, status);
      preTarget = curCached;
    } else {
      log.debug("coupon cache is empty, get coupon from DB {}, {}", userId, status);
      List<Coupon> dbCoupons = couponRepository
        .findAllByUserIdAndStatus(userId, CouponStatus.of(status));

      if (CollectionUtils.isEmpty(dbCoupons)) {
        log.debug("current user doesnt have coupon {}, {}", userId, status);
        return dbCoupons;
      }

      // @formatter:off
      Map<Long, CouponTemplateSDK> id2TemplateSDK = templateClient
        .findIds2TemplateSDK(dbCoupons.stream().map(Coupon::getId).collect(Collectors.toList()))
        .getData();

      dbCoupons.forEach(dc ->
        dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId()))
      );
      // @formatter:on

      preTarget = dbCoupons;

      // write back to redis
      redisService.addCouponToCache(userId, preTarget, status);
    }

    // remove invalid coupon
    preTarget = preTarget.stream().filter(c -> c.getId() != -1L).collect(Collectors.toList());

    if (CouponStatus.of(status).equals(CouponStatus.USABLE)) {
      CouponClassify classify = CouponClassify.classify(preTarget);
      // handle expired coupons
      if (!CollectionUtils.isEmpty(classify.getExpired())) {
        log.info("add expired coupons to cache from findCouponsByStatus");
        redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());

        // request kafka
        kafkaTemplate.send(Constant.TOPIC, JSON.toJSONString(new CouponKafkaMessage(
          CouponStatus.EXPIRED.getCode(),
          classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList())
        )));
      }
    }
    return preTarget;
  }

  @Override
  public List<CouponTemplateSDK> findAvailableTemplates(Long userId) throws CouponException {
    long currentTime = Timestamp.valueOf(LocalDateTime.now()).getTime();
    List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();
    log.debug("Find all template SDK from template client, count: {}", templateSDKS.size());

    // filter expired coupons
    templateSDKS = templateSDKS.stream()
      .filter(t -> t.getRule().getExpiration().getDeadline() > currentTime)
      .collect(Collectors.toList());
    log.info("Find usable template count: {}", templateSDKS.size());

    // key: template id
    // value: key: template limitation, value: coupon template
    Map<Long, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKS.size());
    templateSDKS.forEach(t -> limit2Template.put(t.getId(), Pair.of(t.getRule().getLimitation(), t)));
    List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
    List<Coupon> userUsableCoupons = findCouponsByStatus(userId, CouponStatus.USABLE.getCode());
    log.debug("Current user has usable coupons : {} {}", userId, userUsableCoupons.size());

    Map<Long, List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
      .collect(Collectors.groupingBy(Coupon::getId));

    limit2Template.forEach((k, v) -> {
      int limitation = v.getFirst();
      CouponTemplateSDK templateSDK = v.getSecond();

      if (templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation) {
        return;
      }

      result.add(templateSDK);
    });

    return result;
  }

  @Override
  public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
    return null;
  }

  @Override
  public SettlementInfo settlement(SettlementInfo settlementInfo) throws CouponException {
    return null;
  }

}
