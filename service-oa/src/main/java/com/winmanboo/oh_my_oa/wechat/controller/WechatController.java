package com.winmanboo.oh_my_oa.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.winmanboo.common.jwt.JwtHelper;
import com.winmanboo.common.result.Result;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.auth.service.SysUserService;
import com.winmanboo.vo.wechat.BindPhoneVo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/wechat")
public class WechatController {
  private final SysUserService userService;

  private final WxMpService wxMpService;

  @Value("${wechat.userInfoUrl}")
  private String userInfoUrl;

  @GetMapping("/authorize")
  public String authorize(@RequestParam("returnUrl") String returnUrl, HttpServletRequest request) {
    // 第一个参数：授权路径，在哪个路径下获取微信信息
    // 第二个参数：固定值，授权类型 WxConsts.OAuth2Scope.SNSAPI_USERINFO
    // 第三个参数：授权成功之后，跳转路径，替换 ohmyoa 为 #，因为请求路径中带有#号在回调的时候会有问题
    String redirectUrl = wxMpService.getOAuth2Service().buildAuthorizationUrl(userInfoUrl, WxConsts.OAuth2Scope.SNSAPI_USERINFO,
        URLEncoder.encode(returnUrl.replace("ohmyoa", "#"), StandardCharsets.UTF_8));
    log.info("redirectUrl: {}", redirectUrl);
    return "redirect:" + redirectUrl;
  }

  @GetMapping("/userInfo")
  public String userInfo(@RequestParam("code") String code, @RequestParam("state") String returnUrl) throws WxErrorException {
    // 获取 accessToken
    WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
    // 使用 accessToken 获取 openId
    String openId = accessToken.getOpenId();
    log.info("openId: {}", openId);

    // 获取微信用户信息
    WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
    log.info("微信网页授权】wxMpUser={}", JSON.toJSONString(wxMpUser));

    // 根据 openId 查询用户表
    SysUser sysUser = userService.lambdaQuery().eq(SysUser::getOpenId, openId).one();
    String token = "";
    if (null != sysUser) { // 用户已存在
      token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
    }

    if (!returnUrl.contains("?")) {
      return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
    } else {
      return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
    }
  }

  @ApiOperation(value = "微信账号绑定手机")
  @PostMapping("/bindPhone")
  @ResponseBody
  public Result<String> bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
    // 根据手机号查询数据库
    SysUser sysUser = userService.lambdaQuery().eq(SysUser::getPhone, bindPhoneVo.getPhone()).one();
    // 如果存在更新记录 openId
    if (null != sysUser) {
      sysUser.setOpenId(bindPhoneVo.getOpenId());
      userService.updateById(sysUser);

      String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
      return Result.ok(token);
    } else {
      return Result.fail("手机号码不存在，绑定失败");
    }
  }
}
