package com.winmanboo.oh_my_oa.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winmanboo.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-08
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
  /**
   * @param processId 流程实例id
   * @param status 状态
   * @param description 描述
   */
  void record(Long processId, Integer status, String description);
}
