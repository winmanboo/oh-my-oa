package com.winmanboo.oh_my_oa.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winmanboo.model.process.Process;
import com.winmanboo.vo.process.ApprovalVo;
import com.winmanboo.vo.process.ProcessFormVo;
import com.winmanboo.vo.process.ProcessQueryVo;
import com.winmanboo.vo.process.ProcessVo;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
public interface OaProcessService extends IService<Process> {

  IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

  /**
   * 部署流程定义
   */
  void deployByZip(String deployPath);

  void startUp(ProcessFormVo processFormVo);

  /**
   * 查询代办任务列表
   */
  IPage<ProcessVo> findPending(Page<Process> pageParam);

  Map<String, Object> show(Long processId);

  void approve(ApprovalVo approvalVo);

  IPage<ProcessVo> findProcessed(Page<Process> pageParam);
}
