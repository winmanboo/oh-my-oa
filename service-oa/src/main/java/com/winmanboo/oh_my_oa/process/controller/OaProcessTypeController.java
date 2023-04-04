package com.winmanboo.oh_my_oa.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winmanboo.common.result.Result;
import com.winmanboo.model.process.ProcessType;
import com.winmanboo.oh_my_oa.process.service.OaProcessTypeService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/process/processType")
public class OaProcessTypeController {
  private final OaProcessTypeService processTypeService;

  @ApiOperation("获取分页列表")
  @GetMapping("/{page}/{limit}")
  public Result<IPage<ProcessType>> index(@PathVariable Long page, @PathVariable Long limit) {
    Page<ProcessType> pageParam = new Page<>(page, limit);
    IPage<ProcessType> pageModel = processTypeService.page(pageParam);
    return Result.ok(pageModel);
  }

  @ApiOperation(value = "获取")
  @GetMapping("get/{id}")
  public Result<ProcessType> get(@PathVariable Long id) {
    ProcessType processType = processTypeService.getById(id);
    return Result.ok(processType);
  }

  @ApiOperation(value = "新增")
  @PostMapping("save")
  public Result<Void> save(@RequestBody ProcessType processType) {
    processTypeService.save(processType);
    return Result.ok();
  }

  @ApiOperation(value = "修改")
  @PutMapping("update")
  public Result<Void> updateById(@RequestBody ProcessType processType) {
    processTypeService.updateById(processType);
    return Result.ok();
  }

  @ApiOperation(value = "删除")
  @DeleteMapping("remove/{id}")
  public Result<Void> remove(@PathVariable Long id) {
    processTypeService.removeById(id);
    return Result.ok();
  }
}
