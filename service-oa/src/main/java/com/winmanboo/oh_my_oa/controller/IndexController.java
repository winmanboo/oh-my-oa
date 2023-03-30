package com.winmanboo.oh_my_oa.controller;

import com.winmanboo.common.result.Result;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api("后台登陆管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
  @PostMapping("/login")
  public Result<String> login() {
    return Result.ok("admin-token");
  }

  @GetMapping("/info")
  public Result<Map<String, Object>> info() {
    return Result.ok(Map.of(
        "roles", "[admin]",
        "name", "admin",
        "avatar", "https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg"
    ));
  }

  @PostMapping("/logout")
  public Result<Void> logou() {
    return Result.ok();
  }
}
