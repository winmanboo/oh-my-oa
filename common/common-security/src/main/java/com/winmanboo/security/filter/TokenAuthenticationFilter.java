package com.winmanboo.security.filter;

import com.winmanboo.common.jwt.JwtHelper;
import com.winmanboo.common.result.Result;
import com.winmanboo.common.result.ResultCodeEnum;
import com.winmanboo.common.utils.ResponseUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private final PathMatcher pathMatcher = new AntPathMatcher();

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
        return UsernamePasswordAuthenticationToken.authenticated(username, null, Collections.emptyList());
      }
    }
    return null;
  }
}
