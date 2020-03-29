package com.coupon.template.serialization;

import com.alibaba.fastjson.JSON;
import com.coupon.template.entity.CouponTemplate;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {

  @Override
  public void serialize(CouponTemplate couponTemplate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

    // start serializing
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("id", couponTemplate.getId().toString());
    jsonGenerator.writeStringField("name", couponTemplate.getName());
    jsonGenerator.writeStringField("logo", couponTemplate.getLogo());
    jsonGenerator.writeStringField("desc", couponTemplate.getDesc());
    jsonGenerator.writeStringField("category", couponTemplate.getCategory().getDescription());
    jsonGenerator.writeStringField("productLine", couponTemplate.getProductLine().getDescription());
    jsonGenerator.writeStringField("count", couponTemplate.getCount().toString());
    // @formatter:off
    jsonGenerator.writeStringField("createTime",
      couponTemplate.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    // @formatter:on
    jsonGenerator.writeStringField("userId", couponTemplate.getUserId().toString());
    // @formatter:off
    jsonGenerator.writeStringField("key", couponTemplate.getKey() +
      String.format("%04d", couponTemplate.getId()));
    // @formatter:on
    jsonGenerator.writeStringField("target", couponTemplate.getTarget().getDescription());
    jsonGenerator.writeStringField("rule", JSON.toJSONString(couponTemplate.getRule()));

    // end serializing
    jsonGenerator.writeEndObject();
  }

}
