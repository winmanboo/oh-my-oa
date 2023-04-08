package com.winmanboo.oh_my_oa.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wechat")
public class WechatAccountProperties {
  private String mpAppId;

  private String mpAppSecret;
}
