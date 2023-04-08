package com.winmanboo.security.filter;

import com.alibaba.fastjson.JSON;
import com.winmanboo.common.jwt.JwtHelper;
import com.winmanboo.common.result.Result;
import com.winmanboo.common.result.ResultCodeEnum;
import com.winmanboo.common.utils.ResponseUtils;
import com.winmanboo.security.helper.LoginUserInfoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private final PathMatcher pathMatcher = new AntPathMatcher();

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response,
                                  @Nonnull FilterChain filterChain) throws ServletException, IOException {
    if (pathMatcher.match("/admin/system/index/login", request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    Authentication authentication = getAuthentication(request);
    if (null != authentication) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } else {
      ResponseUtils.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
    }
  }

  private Authentication getAuthentication(HttpServletRequest request) {
    String token = request.getHeader("token");
    if (StringUtils.hasText(token)) {
      String username = JwtHelper.getUsername(token);
      if (StringUtils.hasText(username)) {
        // 将当前用户信息放到 ThreadLocal 中
        LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
        LoginUserInfoHelper.setUsername(JwtHelper.getUsername(token));
        // 通过用户名从 redis 中获取权限数据
        String authorityStr = (String) redisTemplate.opsForValue().get(username);
        if (StringUtils.hasText(authorityStr)) {
          List<Map> maps = JSON.parseArray(authorityStr, Map.class);
          List<SimpleGrantedAuthority> authorities = maps.stream()
              .map(map -> new SimpleGrantedAuthority((String) map.get("authority"))).collect(Collectors.toList());
          return UsernamePasswordAuthenticationToken.authenticated(username, null, authorities);
        } else {
          return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        }
      }
    }
    return null;
  }
}
