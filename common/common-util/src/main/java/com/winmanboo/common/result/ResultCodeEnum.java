package com.winmanboo.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultCodeEnum {
  SUCCESS(200, "成功"),
  FAIL(201, "失败"),
  SERVICE_ERROR(2012, "服务异常"),
  DATA_ERROR(204, "数据异常"),
  LOGIN_AUTH(208, "未登陆"),
  PERMISSION(209, "没有权限");

  private final int code;

  private final String message;

}
