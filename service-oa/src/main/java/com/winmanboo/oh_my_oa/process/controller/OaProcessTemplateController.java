package com.winmanboo.oh_my_oa.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winmanboo.common.result.Result;
import com.winmanboo.model.process.ProcessTemplate;
import com.winmanboo.oh_my_oa.process.service.OaProcessTemplateService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/process/processTemplate")
public class OaProcessTemplateController {
  private final OaProcessTemplateService processTemplateService;

  // 分页查询审批模版
  @ApiOperation("获取分页审批模版的数据")
  @GetMapping("/{page}/{limit}")
  public Result<IPage<ProcessTemplate>> index(@PathVariable Long page, @PathVariable Long limit) {
    Page<ProcessTemplate> pageParam = new Page<>(page, limit);
    // 分页查询审批模版，把审批类型对应的名称查询出来
    IPage<ProcessTemplate> pageModel = processTemplateService.selectPageProcessTemplate(pageParam);
    return Result.ok(pageModel);
  }

  //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
  @ApiOperation(value = "获取")
  @GetMapping("get/{id}")
  public Result<ProcessTemplate> get(@PathVariable Long id) {
    ProcessTemplate processTemplate = processTemplateService.getById(id);
    return Result.ok(processTemplate);
  }

  //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
  @ApiOperation(value = "新增")
  @PostMapping("save")
  public Result<Void> save(@RequestBody ProcessTemplate processTemplate) {
    processTemplateService.save(processTemplate);
    return Result.ok();
  }

  //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
  @ApiOperation(value = "修改")
  @PutMapping("update")
  public Result<Void> updateById(@RequestBody ProcessTemplate processTemplate) {
    processTemplateService.updateById(processTemplate);
    return Result.ok();
  }

  //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
  @ApiOperation(value = "删除")
  @DeleteMapping("remove/{id}")
  public Result<Void> remove(@PathVariable Long id) {
    processTemplateService.removeById(id);
    return Result.ok();
  }
}
