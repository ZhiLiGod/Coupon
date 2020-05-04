package com.coupon.distribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.coupon.distribution.constant.CouponStatus;
import com.coupon.distribution.dto.CouponKafkaMessage;
import com.coupon.distribution.entity.Coupon;
import com.coupon.distribution.repository.CouponRepository;
import com.coupon.distribution.service.IKafkaService;
import com.couponcommon.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class KafkaServiceImpl implements IKafkaService {

  @Autowired
  private CouponRepository couponRepository;

  @Override
  @KafkaListener(topics = { Constant.TOPIC }, groupId = "coupon")
  public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
    Optional<?> kafkaMessage = Optional.ofNullable(record.value());

    if (kafkaMessage.isPresent()) {
      Object message = kafkaMessage.get();
      CouponKafkaMessage couponKafkaMessage = JSON.parseObject(message.toString(), CouponKafkaMessage.class);

      log.info("Receive Coupon Kafka Message: {}", message.toString());
      CouponStatus status = CouponStatus.of(couponKafkaMessage.getStatus());

      switch(status) {
      case USED:
        processUsedCoupons(couponKafkaMessage, status);
        break;
      case EXPIRED:
        processExpiredCoupons(couponKafkaMessage, status);
        break;
      default:
        break;
      }
    }
  }

  private void processUsedCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
    persistCouponsByStatus(kafkaMessage, status);
  }

  private void processExpiredCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
    persistCouponsByStatus(kafkaMessage, status);
  }

  private void persistCouponsByStatus(CouponKafkaMessage kafkaMessage, CouponStatus status) {
    List<Coupon> coupons = couponRepository.findAllById(kafkaMessage.getIds());
    if (CollectionUtils.isEmpty(coupons) || coupons.size() != kafkaMessage.getIds().size()) {
      log.error("Cannot find correct coupon info");
      return;
    }

    coupons.forEach(c -> c.setStatus(status));
    couponRepository.saveAll(coupons);
  }

}
