package com.coupon.distribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.coupon.distribution.constant.CouponStatus;
import com.coupon.distribution.entity.Coupon;
import com.coupon.distribution.service.IRedisService;
import com.couponcommon.constant.Constant;
import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Override
  public List<Coupon> getCachedCoupons(Long userId, Integer status) {
    String redisKey = status2RedisKey(status, userId);
    List<String> couponStrings = redisTemplate.opsForHash().values(redisKey)
      .stream()
      .map(o -> Objects.toString(o, null))
      .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(couponStrings)) {
      saveEmptyCouponList2Cache(userId, Collections.singletonList(status));
      return Collections.emptyList();
    }

    return couponStrings.stream()
      .map(c -> JSON.parseObject(c, Coupon.class))
      .collect(Collectors.toList());
  }

  @Override
  public void saveEmptyCouponList2Cache(Long userId, List<Integer> statuses) {
    log.info("Save Empty List to Cache for User: {}, Status: {}", userId, JSON.toJSON(statuses));

    Map<String, String> invalidCouponMap = new HashMap<>();
    invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));
    SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {

      @SuppressWarnings("all")
      @Override
      public Object execute(RedisOperations operations) throws DataAccessException {
        statuses.forEach(s -> {
          String redisKey = status2RedisKey(s, userId);
          operations.opsForHash().putAll(redisKey, invalidCouponMap);
        });

        return null;
      }
    };

    log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
  }

  @Override
  public String tryToAcquireCouponCodeFromCache(Integer templateId) {
    return null;
  }

  @Override
  public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
    return null;
  }

  private String status2RedisKey(Integer status, Long userId) {
    String redisKey = null;

    switch (CouponStatus.of(status)) {
    case USABLE:
      redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE, userId);
      break;
    case USED:
      redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
      break;
    case EXPIRED:
      redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
      break;
    default:
      break;
    }

    return redisKey;
  }

}
