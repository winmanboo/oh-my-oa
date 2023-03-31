package com.winmanboo.common.exception;

import com.winmanboo.common.result.ResultCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OhMyOaException extends RuntimeException {
  private Integer code;

  private String msg;

  public OhMyOaException(Integer code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public OhMyOaException(String msg) {
    super(msg);
    this.code = ResultCodeEnum.FAIL.getCode();
    this.msg = msg;
  }

  public OhMyOaException(ResultCodeEnum resultCodeEnum) {
    super(resultCodeEnum.getMessage());
    this.code = resultCodeEnum.getCode();
    this.msg = resultCodeEnum.getMessage();
  }
}
