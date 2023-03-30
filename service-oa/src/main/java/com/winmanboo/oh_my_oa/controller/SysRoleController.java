package com.winmanboo.oh_my_oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winmanboo.common.result.Result;
import com.winmanboo.model.system.SysRole;
import com.winmanboo.oh_my_oa.service.SysRoleService;
import com.winmanboo.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
@RequiredArgsConstructor
public class SysRoleController {
  private final SysRoleService sysRoleService;

  /**
   * 查询所有角色
   */
  @GetMapping("/findAll")
  @ApiOperation("查询所有接口")
  public Result<List<SysRole>> findAll() {
    return Result.ok(sysRoleService.list());
  }

  /**
   * 获取角色分页信息
   * @param page 当前页
   * @param limit 每页显示记录数
   * @param sysRoleQueryVo
   * @return
   */
  @ApiOperation("条件分页查询")
  @GetMapping("/{page}/{limit}")
  public Result<IPage<SysRole>> pageQueryRole(@PathVariable("page") Long page,
                              @PathVariable("limit") Long limit,
                              SysRoleQueryVo sysRoleQueryVo) {
    Page<SysRole> pageParam = new Page<>(page, limit);
    LambdaQueryWrapper<SysRole> wrapper = Wrappers.lambdaQuery();
    String roleName = sysRoleQueryVo.getRoleName();
    if (StringUtils.hasText(roleName)) {
      wrapper.like(SysRole::getRoleName, roleName);
    }

    IPage<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
    return Result.ok(pageModel);
  }

  @ApiOperation("添加角色")
  @PostMapping("/save")
  public Result<Void> save(@RequestBody SysRole role) {
    return sysRoleService.save(role) ? Result.ok() : Result.fail();
  }

  @ApiOperation("根据id查询")
  @GetMapping("/get/{id}")
  public Result<SysRole> get(@PathVariable("id") Long id) {
    return Result.ok(sysRoleService.getById(id));
  }

  @ApiOperation("修改角色")
  @PutMapping("/update")
  public Result<Void> update(@RequestBody SysRole sysRole) {
    return sysRoleService.updateById(sysRole) ? Result.ok() : Result.fail();
  }

  @ApiOperation("根据id删除")
  @DeleteMapping("/remove/{id}")
  public Result<Void> remove(@PathVariable("id") Long id) {
    return sysRoleService.removeById(id) ? Result.ok() : Result.fail();
  }

  // 数组 [1,2,3]
  @ApiOperation("批量删除")
  @DeleteMapping("/batchRemove")
  public Result<Void> batchRemove(@RequestBody List<Long> ids) {
    return sysRoleService.removeByIds(ids) ? Result.ok() : Result.fail();
  }
}