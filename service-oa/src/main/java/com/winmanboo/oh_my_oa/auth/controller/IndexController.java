package com.winmanboo.oh_my_oa.auth.controller;

import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.common.jwt.JwtHelper;
import com.winmanboo.common.result.Result;
import com.winmanboo.common.utils.MD5;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.auth.service.SysMenuService;
import com.winmanboo.oh_my_oa.auth.service.SysUserService;
import com.winmanboo.vo.system.LoginVo;
import com.winmanboo.vo.system.RouterVo;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api("后台登陆管理")
@RestController
@RequestMapping("/admin/system/index")
@RequiredArgsConstructor
public class IndexController {
  private final SysUserService sysUserService;

  private final SysMenuService sysMenuService;

  @PostMapping("/login")
  public Result<Map<String, Object>> login(@RequestBody LoginVo loginVo) {
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

    return Result.ok(Map.of("token", token));
  }

  @GetMapping("/info")
  public Result<Map<String, Object>> info(HttpServletRequest request) {
    // 1、从请求头中获取用户信息（获取请求头 token 字符串）
    String token = request.getHeader("token");

    // 2、从 token 字符串获取用户 id 或者用户名称
    Long userId = JwtHelper.getUserId(token);

    // 3、根据用户 id 查询数据库，把用户信息获取出来
    SysUser user = sysUserService.getById(userId);

    // 4、根据用户 id 获取用户可以操作的菜单列表
    // 查询数据库动态构建路由接口，进行显示
    List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);

    // 5、根据用户 id 获取用户可以操作的按钮列表
    List<String> permsList = sysMenuService.findUserPermsByUserId(userId);

    // 6、返回相应的数据

    return Result.ok(Map.of(
        "roles", "[admin]",
        "name", user.getName(),
        "avatar", "https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg",
        // 返回可以操作的按钮
        "buttons", permsList,
        // 返回可以操作的菜单
        "routers", routerList
    ));
  }

  @PostMapping("/logout")
  public Result<Void> logout() {
    return Result.ok();
  }
}
