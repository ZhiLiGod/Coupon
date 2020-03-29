package com.coupon.template.service.impl;

import com.coupon.template.entity.CouponTemplate;
import com.coupon.template.repository.CouponTemplateRepository;
import com.coupon.template.service.IAsyncService;
import com.couponcommon.constant.Constant;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

  private final CouponTemplateRepository templateRepository;
  private final StringRedisTemplate redisTemplate;

  @Autowired
  public AsyncServiceImpl(CouponTemplateRepository templateRepository, StringRedisTemplate redisTemplate) {
    this.templateRepository = templateRepository;
    this.redisTemplate = redisTemplate;
  }

  @SuppressWarnings("all")
  @Transactional
  @Async("getAsyncExecutor")
  @Override
  public void asyncConstructCouponByTemplate(CouponTemplate template) {

    Stopwatch watch = Stopwatch.createStarted();
    Set<String> couponCodes = buildCouponCode(template);

    // redis
    // @formatter:off
    String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE
      , template.getId().toString());

    log.info("Push CouponCode to Redis: {}",
      redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
    // @formatter:on
    template.setAvailable(Boolean.TRUE);
    templateRepository.save(template);

    watch.stop();
    log.info("Construct CouponCode By Template Cost {}ms", watch.elapsed(TimeUnit.MICROSECONDS));

    // TODO: send sms or email to notify template is ready
  }

  @SuppressWarnings("all")
  private Set<String> buildCouponCode(CouponTemplate template) {
    Stopwatch watch = Stopwatch.createStarted();
    Set<String> result = new HashSet<>(template.getCount());

    // @formatter:off
    String prefixFirst4 = template.getProductLine().getCode().toString()
      + template.getCategory().getCode();
    // @formatter:on

    String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

    for (int i = 0; i != template.getCount(); ++i) {
      result.add(prefixFirst4 + buildCouponCodeSuffixLast14(dateNow));
    }

    while (result.size() < template.getCount()) {
      result.add(prefixFirst4 + buildCouponCodeSuffixLast14(dateNow));
    }

    assert result.size() == template.getCount();
    watch.stop();
    log.info("Build Coupon Code Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

    return result;
  }

  private String buildCouponCodeSuffixLast14(String date) {
    char[] bases = new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
    Collections.shuffle(chars);
    String mid6 = chars.stream().map(Objects::toString).collect(Collectors.joining());
    String last8 = RandomStringUtils.random(1, bases)
      + RandomStringUtils.randomNumeric(7);
    return mid6 + last8;
  }

}
