package com.coupon.distribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.coupon.distribution.constant.CouponStatus;
import com.coupon.distribution.dto.CouponKafkaMessage;
import com.coupon.distribution.service.IKafkaService;
import com.couponcommon.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class KafkaServiceImpl implements IKafkaService {

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
      case USABLE:
        break;
      case USED:
        break;
      case EXPIRED:
        break;
      }
    }
  }

}
