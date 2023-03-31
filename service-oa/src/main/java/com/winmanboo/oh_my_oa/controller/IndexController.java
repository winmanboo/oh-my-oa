package com.winmanboo.oh_my_oa.controller;

import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.common.jwt.JwtHelper;
import com.winmanboo.common.result.Result;
import com.winmanboo.common.utils.MD5;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.service.SysUserService;
import com.winmanboo.vo.system.LoginVo;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api("后台登陆管理")
@RestController
@RequestMapping("/admin/system/index")
@RequiredArgsConstructor
public class IndexController {
  private final SysUserService sysUserService;

  @PostMapping("/login")
  public Result<String> login(@RequestBody LoginVo loginVo) {
    String username = loginVo.getUsername();
    SysUser user = sysUserService.lambdaQuery().eq(SysUser::getUsername, username).one();
    if (user == null) {
      throw new OhMyOaException("用户不存在！");
    }
    String encrypt = MD5.encrypt(loginVo.getPassword());
    if (!encrypt.equals(user.getPassword())) {
      throw new OhMyOaException("密码错误！");
    }

    if (user.getStatus() == 0) {
      throw new OhMyOaException("用户已经被禁用，请联系管理员！");
    }
    String token = JwtHelper.createToken(user.getId(), user.getUsername());

    return Result.ok(token);
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
