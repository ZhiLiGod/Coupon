package com.coupon.distribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.coupon.distribution.constant.CouponStatus;
import com.coupon.distribution.entity.Coupon;
import com.coupon.distribution.service.IRedisService;
import com.couponcommon.constant.Constant;
import com.couponcommon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
    return redisTemplate.opsForList().leftPop(redisKey);
  }

  @Override
  public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
    Integer result = -1;

    switch (CouponStatus.of(status)) {
    case USABLE:
      result = addCoupon2CacheForUsable(userId, coupons);
      break;
    case USED:
      result = addCoupon2CacheForUsed(userId, coupons);
      break;
    case EXPIRED:
      break;
    }

    return result;
  }

  private Integer addCoupon2CacheForUsable(Long userId, List<Coupon> coupons) {
    Map<String, String> needCachedObjects = new HashMap<>();
    coupons.forEach(c -> needCachedObjects.put(c.getId().toString(), JSON.toJSONString(c)));
    String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
    redisTemplate.opsForHash().putAll(redisKey, needCachedObjects);
    redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
    return needCachedObjects.size();
  }

  private Integer addCoupon2CacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {
    Map<String, String> needCachedForUsed = new HashMap<>();
    String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
    String redisKeyForUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

    List<Coupon> currentUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
    // count must greater than 1 (1 is empty coupon)
    assert currentUsableCoupons.size() > coupons.size();
    coupons.forEach(c -> needCachedForUsed.put(c.getId().toString(), JSON.toJSONString(c)));

    List<Long> currentUsableIds = currentUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList()) ;
    List<Long> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());

    if (paramIds.stream().anyMatch(p -> !currentUsableIds.contains(p))) {
      log.error("Current Coupons is Not Equal toCache");
      throw new CouponException("Invalid coupons");
    }

    SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {

      @SuppressWarnings("all")
      @Override
      public Object execute(RedisOperations operations) throws DataAccessException {
        operations.opsForHash().putAll(redisKeyForUsed, needCachedForUsed);
        operations.opsForHash().delete(redisKeyForUsable, paramIds.stream().map(Object::toString).toArray());
        operations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
        operations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
        return null;
      }
    };

    log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    return coupons.size();
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

  /**
   * get a random expiration time, avoid cache avalanche
   * @param min hour
   * @param max hour
   * @return [min, max] random second
   */
  private Long getRandomExpirationTime(Integer min, Integer max) {
    return RandomUtils.nextLong(min * 60 * 60, max * 60 * 60);
  }

}
