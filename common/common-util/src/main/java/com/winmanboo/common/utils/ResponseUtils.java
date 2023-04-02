package com.winmanboo.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winmanboo.common.result.Result;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@UtilityClass
public class ResponseUtils {
  public static void out(HttpServletResponse response, Result<Object> result) {
    ObjectMapper mapper = new ObjectMapper();
    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    try {
      mapper.writeValue(response.getWriter(), result);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
