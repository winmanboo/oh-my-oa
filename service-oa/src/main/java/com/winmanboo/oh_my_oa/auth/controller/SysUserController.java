package com.winmanboo.oh_my_oa.auth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winmanboo.common.result.Result;
import com.winmanboo.common.utils.MD5;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.auth.service.SysUserService;
import com.winmanboo.vo.system.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author winmanboo
 * @since 2023-03-31
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
@RequiredArgsConstructor
public class SysUserController {
  private final SysUserService service;

  @ApiOperation("更新状态")
  @GetMapping("/updateStatus/{id}/{status}")
  public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
    service.updateStatus(id, status);
    return Result.ok();
  }

  @ApiOperation("用户条件分页查询")
  @GetMapping("/{page}/{limit}")
  public Result<IPage<SysUser>> index(@PathVariable("page") Long page, @PathVariable("limit") Long limit, SysUserQueryVo sysUserQueryVo) {
    IPage<SysUser> pageParam = new Page<>(page, limit);
    return Result.ok(service.page(pageParam, Wrappers.<SysUser>lambdaQuery()
        .like(StringUtils.hasText(sysUserQueryVo.getKeyword()), SysUser::getUsername, sysUserQueryVo.getKeyword())
        .ge(StringUtils.hasText(sysUserQueryVo.getCreateTimeBegin()), SysUser::getCreateTime, sysUserQueryVo.getCreateTimeBegin())
        .le(StringUtils.hasText(sysUserQueryVo.getCreateTimeEnd()), SysUser::getCreateTime, sysUserQueryVo.getCreateTimeEnd())
    ));
  }

  @ApiOperation(value = "获取用户")
  @GetMapping("get/{id}")
  public Result<SysUser> get(@PathVariable Long id) {
    SysUser user = service.getById(id);
    return Result.ok(user);
  }

  @ApiOperation(value = "保存用户")
  @PostMapping("save")
  public Result<Void> save(@RequestBody SysUser user) {
    String encrypt = MD5.encrypt(user.getPassword());
    user.setPassword(encrypt);
    service.save(user);
    return Result.ok();
  }

  @ApiOperation(value = "更新用户")
  @PutMapping("update")
  public Result<Void> updateById(@RequestBody SysUser user) {
    service.updateById(user);
    return Result.ok();
  }

  @ApiOperation(value = "删除用户")
  @DeleteMapping("remove/{id}")
  public Result<Void> remove(@PathVariable Long id) {
    service.removeById(id);
    return Result.ok();
  }

  @ApiOperation("当前用户信息")
  @GetMapping("/getCurrentUser")
  public Result<Map<String, Object>> getCurrentUser() {
    Map<String, Object> map = service.getCurrentUser();
    return Result.ok(map);
  }
}
