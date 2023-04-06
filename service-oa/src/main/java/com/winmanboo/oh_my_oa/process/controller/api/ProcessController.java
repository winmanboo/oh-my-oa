package com.winmanboo.oh_my_oa.process.controller.api;

import com.winmanboo.common.result.Result;
import com.winmanboo.model.process.ProcessType;
import com.winmanboo.oh_my_oa.process.service.OaProcessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("审批流管理")
@RestController
@RequestMapping("/admin/process")
@CrossOrigin // 跨域
@RequiredArgsConstructor
public class ProcessController {
  private final OaProcessTypeService processTypeService;

  @ApiOperation("查询所有的审批分类和每个分类所有的审批模版")
  @GetMapping("/findProcessType")
  public Result<List<ProcessType>> findProcessType() {
    List<ProcessType> processTypeList = processTypeService.findProcessType();
    return Result.ok(processTypeList);
  }
}
