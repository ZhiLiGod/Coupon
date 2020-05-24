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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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

  /**
   * user get token
   * @param request {@link AcquireTemplateRequest}
   * @return {@link Coupon}
   * @throws CouponException {@link CouponException}
   */
  @Override
  public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
    // @formatter: off
    Map<Long, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
      Collections.singletonList(request.getTemplateSDK().getId())
    ).getData();
    // @formatter:on

    if (id2Template.size() <= 0) {
      log.error("Cannot acquire template from template client");
      throw new CouponException("Cannot acquire template from template client");
    }

    // check if user is able to get this coupon
    List<Coupon> userUsableCoupons = findCouponsByStatus(request.getUserId(), CouponStatus.USABLE.getCode());
    Map<Long, List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
      .collect(Collectors.groupingBy(Coupon::getId));

    if (templateId2Coupons.containsKey(request.getTemplateSDK().getId()) &&
      templateId2Coupons.get(request.getTemplateSDK().getId()).size() >= request.getTemplateSDK().getRule().getLimitation()) {
      log.error("Exceed template assign limitation: {}", request.getTemplateSDK().getId());
      throw new CouponException("Exceed template assign limitation: " + request.getTemplateSDK().getId());
    }

    String couponCode = redisService.tryToAcquireCouponCodeFromCache(request.getTemplateSDK().getId().intValue());
    if (StringUtils.isEmpty(couponCode)) {
      log.error("Cannot require coupon code");
      throw new CouponException("Cannot require coupon code");
    }

    Coupon newCoupon = new Coupon(
      request.getTemplateSDK().getId(), request.getUserId(), couponCode, CouponStatus.USABLE
    );
    newCoupon = couponRepository.save(newCoupon);

    // fill in coupon template sdk and then save in cache
    newCoupon.setTemplateSDK(request.getTemplateSDK());
    redisService.addCouponToCache(request.getUserId(), Collections.singletonList(newCoupon), CouponStatus.USABLE.getCode());

    return newCoupon;
  }

  @Override
  public SettlementInfo settlement(SettlementInfo settlementInfo) throws CouponException {
    // if no coupon, return full price
    List<SettlementInfo.CouponAndTemplateInfo> ctInfos = settlementInfo.getCouponAndTemplateInfos();
    if (CollectionUtils.isEmpty(ctInfos)) {
      // @formatter:off
      BigDecimal total = settlementInfo.getGoodsInfos().stream()
        .map(g -> g.getPrice().multiply(BigDecimal.valueOf(g.getCount())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

      settlementInfo.setCost(retain2Decimals(total));
    }

    // check if the token is belong to user
    List<Coupon> coupons = findCouponsByStatus(settlementInfo.getUserId(),
      CouponStatus.USABLE.getCode());
    Map<Long, Coupon> id2Coupon = coupons.stream()
      .collect(Collectors.toMap(Coupon::getId, Function.identity()));

    if (id2Coupon.isEmpty()) {
      log.error("User coupon has some problem, it is not sub collection of coupons");
      throw new CouponException("User coupon has some problem, it is not sub collection of coupons");
    }

    log.debug("Current settlement coupons are belong to user");
    List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
    ctInfos.forEach(c -> settleCoupons.add(id2Coupon.get(c.getId())));

    SettlementInfo processedInfo = settlementClient.processRule(settlementInfo).getData();
    if (processedInfo.isEmploy() && !CollectionUtils.isEmpty(processedInfo.getCouponAndTemplateInfos())) {
      log.info("Settle user coupon: {}", settlementInfo.getUserId());
      // update cache
      redisService.addCouponToCache(settlementInfo.getUserId(), settleCoupons, CouponStatus.USED.getCode());
      // update db
      // @formatter:off
      kafkaTemplate.send(Constant.TOPIC,
        JSON.toJSONString(new CouponKafkaMessage(CouponStatus.USED.getCode(),
          settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList()))));
      // @formatter:on
    }

    return processedInfo;
  }

  private BigDecimal retain2Decimals(BigDecimal value) {
    return value.setScale(2, BigDecimal.ROUND_HALF_UP);
  }

}