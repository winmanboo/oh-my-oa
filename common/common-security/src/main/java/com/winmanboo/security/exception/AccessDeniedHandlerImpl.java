package com.winmanboo.security.exception;

import com.winmanboo.common.result.Result;
import com.winmanboo.common.result.ResultCodeEnum;
import com.winmanboo.common.utils.ResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    ResponseUtils.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
  }
}
