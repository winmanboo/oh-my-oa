package com.winmanboo.oh_my_oa.process.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winmanboo.common.result.Result;
import com.winmanboo.model.process.Process;
import com.winmanboo.model.process.ProcessTemplate;
import com.winmanboo.model.process.ProcessType;
import com.winmanboo.oh_my_oa.process.service.OaProcessService;
import com.winmanboo.oh_my_oa.process.service.OaProcessTemplateService;
import com.winmanboo.oh_my_oa.process.service.OaProcessTypeService;
import com.winmanboo.vo.process.ProcessFormVo;
import com.winmanboo.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api("审批流管理")
@RestController
@RequestMapping("/admin/process")
@CrossOrigin // 跨域
@RequiredArgsConstructor
public class ProcessController {
  private final OaProcessTypeService processTypeService;

  private final OaProcessTemplateService processTemplateService;

  private final OaProcessService processService;

  @ApiOperation("代办任务")
  @GetMapping("/findPending/{page}/{limit}")
  public Result<IPage<ProcessVo>> findPending(@ApiParam(name = "page", value = "当前页码", required = true) @PathVariable Long page,
                                            @ApiParam(name = "limit", value = "每页记录数", required = true) @PathVariable Long limit) {
    Page<Process> pageParam = new Page<>(page, limit);
    IPage<ProcessVo> pageModel = processService.findPending(pageParam);
    return Result.ok(pageModel);
  }

  @ApiOperation("启动流程")
  @PostMapping("/startUp")
  public Result<Void> startUp(@RequestBody ProcessFormVo processFormVo) {
    processService.startUp(processFormVo);
    return Result.ok();
  }

  @ApiOperation("获取审批模版数据")
  @GetMapping("/getProcessTemplate/{processTemplateId}")
  public Result<ProcessTemplate> getProcessTemplate(@PathVariable Long processTemplateId) {
    return Result.ok(processTemplateService.getById(processTemplateId));
  }

  @ApiOperation("查询所有的审批分类和每个分类所有的审批模版")
  @GetMapping("/findProcessType")
  public Result<List<ProcessType>> findProcessType() {
    List<ProcessType> processTypeList = processTypeService.findProcessType();
    return Result.ok(processTypeList);
  }

  @ApiOperation("查看审批详情信息")
  @GetMapping("/show/{id}")
  public Result<Map<String, Object>> show(@PathVariable("id") Long processId) {
    Map<String, Object> map = processService.show(processId);
    return Result.ok(map);
  }
}
