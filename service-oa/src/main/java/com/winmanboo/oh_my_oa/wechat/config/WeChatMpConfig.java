package com.winmanboo.oh_my_oa.wechat.config;

import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = WechatAccountProperties.class)
public class WeChatMpConfig {
  private final WechatAccountProperties properties;

  @Bean
  public WxMpService wxMpService() {
    WxMpService wxMpService = new WxMpServiceImpl();
    wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
    return wxMpService;
  }

  @Bean
  public WxMpConfigStorage wxMpConfigStorage() {
    WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
    wxMpConfigStorage.setAppId(properties.getMpAppId());
    wxMpConfigStorage.setSecret(properties.getMpAppSecret());
    return wxMpConfigStorage;
  }
}
