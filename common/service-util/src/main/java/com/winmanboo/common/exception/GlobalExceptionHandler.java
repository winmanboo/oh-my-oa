package com.winmanboo.common.exception;

import com.winmanboo.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(value = Exception.class)
  public Result<Object> defaultHandlerException(Exception e) {
    e.printStackTrace();
    return Result.fail().message(e.getMessage());
  }

  @ExceptionHandler(value = OhMyOaException.class)
  public Result<Object> serviceHandlerException(OhMyOaException e) {
    e.printStackTrace();
    return Result.fail().code(e.getCode()).message(e.getMsg());
  }
}
