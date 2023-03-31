package com.winmanboo.oh_my_oa.controller;

import com.winmanboo.common.result.Result;
import com.winmanboo.model.system.SysMenu;
import com.winmanboo.oh_my_oa.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author winmanboo
 * @since 2023-03-31
 */
@Api(tags = "菜单管理接口")
@RestController
@RequestMapping("/admin/system/sysMenu")
@RequiredArgsConstructor
public class SysMenuController {
  private final SysMenuService sysMenuService;

  @ApiOperation("菜单列表")
  @GetMapping("/findNodes")
  public Result<List<SysMenu>> findNodes() {
    List<SysMenu> list = sysMenuService.findNodes();
    return Result.ok(list);
  }

  @ApiOperation(value = "新增菜单")
  @PostMapping("save")
  public Result<Void> save(@RequestBody SysMenu permission) {
    sysMenuService.save(permission);
    return Result.ok();
  }

  @ApiOperation(value = "修改菜单")
  @PutMapping("update")
  public Result<Void> updateById(@RequestBody SysMenu permission) {
    sysMenuService.updateById(permission);
    return Result.ok();
  }

  @ApiOperation(value = "删除菜单")
  @DeleteMapping("remove/{id}")
  public Result<Void> remove(@PathVariable Long id) {
    sysMenuService.removeById(id);
    return Result.ok();
  }
}
