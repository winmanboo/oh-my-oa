package com.winmanboo.oh_my_oa.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winmanboo.common.result.Result;
import com.winmanboo.oh_my_oa.process.service.OaProcessService;
import com.winmanboo.vo.process.ProcessQueryVo;
import com.winmanboo.vo.process.ProcessVo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/admin/process")
public class OaProcessController {
  private final OaProcessService processService;

  @ApiOperation(value = "获取分页列表")
  @GetMapping("{page}/{limit}")
  public Result<IPage<ProcessVo>> index(@PathVariable Long page,
                                        @PathVariable Long limit,
                                        ProcessQueryVo processQueryVo) {
    Page<ProcessVo> pageParam = new Page<>(page, limit);
    IPage<ProcessVo> pageModel = processService.selectPage(pageParam, processQueryVo);
    return Result.ok(pageModel);
  }
}
