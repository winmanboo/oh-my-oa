package com.winmanboo.security.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winmanboo.common.jwt.JwtHelper;
import com.winmanboo.common.result.Result;
import com.winmanboo.common.result.ResultCodeEnum;
import com.winmanboo.common.utils.ResponseUtils;
import com.winmanboo.security.domain.SecurityUser;
import com.winmanboo.vo.system.LoginVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {
  private final RedisTemplate<String, Object> redisTemplate;

  // 构造方法
  public TokenLoginFilter(AuthenticationManager authenticationManager, RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.setAuthenticationManager(authenticationManager);
    this.setPostOnly(false);
    // 指定登陆接口及提交方式，可以指定任意路径
    this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/system/index/login",
        "POST"));
  }

  // 登陆认证
  // 获取输入的用户名和密码，调用方法认证
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    try {
      LoginVo loginVo = new ObjectMapper().readValue(request.getInputStream(), LoginVo.class);
      Authentication unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(loginVo.getUsername(),
          loginVo.getPassword());
      return this.getAuthenticationManager().authenticate(unauthenticated);
    } catch (IOException e) {
      throw new AuthenticationServiceException("读取数据失败");
    }
  }

  // 认证成功调用方法
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                          Authentication authResult) throws IOException, ServletException {
    // 获取当前用户
    SecurityUser principal = (SecurityUser) authResult.getPrincipal();
    // 生成 token 字符串
    String token = JwtHelper.createToken(principal.getSysUser().getId(), principal.getSysUser().getUsername());

    // 获取当前用户权限数据，存到 redis 中，key-username value-权限数据
    redisTemplate.opsForValue().set(principal.getUsername(), JSON.toJSONString(principal.getAuthorities()));
    // 返回
    ResponseUtils.out(response, Result.ok(Map.of("token", token)));
  }

  // 认证失败调用方法
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException failed) throws IOException, ServletException {
    ResponseUtils.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
  }
}
