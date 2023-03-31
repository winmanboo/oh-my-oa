package com.winmanboo.oh_my_oa.controller;

import com.winmanboo.common.result.Result;
import com.winmanboo.model.system.SysMenu;
import com.winmanboo.oh_my_oa.service.SysMenuService;
import com.winmanboo.vo.system.AssignMenuVo;
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

  @ApiOperation("查询所有菜单和角色分配的菜单")
  @GetMapping("/toAssign/{roleId}")
  public Result<List<SysMenu>> toAssign(@PathVariable Long roleId) {
    List<SysMenu> list = sysMenuService.findMenuByRoleId(roleId);
    return Result.ok(list);
  }

  @ApiOperation("为角色分配菜单")
  @PostMapping("/doAssign")
  public Result<Void> doAssign(@RequestBody AssignMenuVo assignMenuVo) {
    sysMenuService.doAssign(assignMenuVo);
    return Result.ok();
  }

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
    // TODO: 2023/3/31 删除菜单与角色的关联关系
    sysMenuService.removeMenuById(id);
    return Result.ok();
  }
}
