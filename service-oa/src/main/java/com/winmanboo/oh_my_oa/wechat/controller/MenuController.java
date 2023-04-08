package com.winmanboo.oh_my_oa.wechat.controller;

import com.winmanboo.common.result.Result;
import com.winmanboo.model.wechat.Menu;
import com.winmanboo.oh_my_oa.wechat.service.MenuService;
import com.winmanboo.vo.wechat.MenuVo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-08
 */
@RestController
@RequestMapping("/admin/wechat/menu")
@RequiredArgsConstructor
public class MenuController {
  private final MenuService menuService;

  //@PreAuthorize("hasAuthority('bnt.menu.list')")
  @ApiOperation(value = "获取")
  @GetMapping("get/{id}")
  public Result<Menu> get(@PathVariable Long id) {
    Menu menu = menuService.getById(id);
    return Result.ok(menu);
  }

  //@PreAuthorize("hasAuthority('bnt.menu.add')")
  @ApiOperation(value = "新增")
  @PostMapping("save")
  public Result<Void> save(@RequestBody Menu menu) {
    menuService.save(menu);
    return Result.ok();
  }

  //@PreAuthorize("hasAuthority('bnt.menu.update')")
  @ApiOperation(value = "修改")
  @PutMapping("update")
  public Result<Void> updateById(@RequestBody Menu menu) {
    menuService.updateById(menu);
    return Result.ok();
  }

  //@PreAuthorize("hasAuthority('bnt.menu.remove')")
  @ApiOperation(value = "删除")
  @DeleteMapping("remove/{id}")
  public Result<Void> remove(@PathVariable Long id) {
    menuService.removeById(id);
    return Result.ok();
  }

  @ApiOperation("获取全部菜单")
  @GetMapping("/findMenuInfo")
  public Result<List<MenuVo>> findMenuInfo() {
    List<MenuVo> menuVoList = menuService.findMenuInfo();
    return Result.ok(menuVoList);
  }
}
