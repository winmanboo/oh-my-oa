package com.winmanboo.security.exception;

import com.winmanboo.common.result.Result;
import com.winmanboo.common.result.ResultCodeEnum;
import com.winmanboo.common.utils.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    ResponseUtils.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
  }
}
