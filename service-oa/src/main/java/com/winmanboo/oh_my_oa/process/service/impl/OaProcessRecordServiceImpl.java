package com.winmanboo.oh_my_oa.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.process.ProcessRecord;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.auth.service.SysUserService;
import com.winmanboo.oh_my_oa.process.mapper.OaProcessRecordMapper;
import com.winmanboo.oh_my_oa.process.service.OaProcessRecordService;
import com.winmanboo.security.helper.LoginUserInfoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-08
 */
@Service
@RequiredArgsConstructor
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {
  private final SysUserService userService;

  @Override
  public void record(Long processId, Integer status, String description) {
    Long userId = LoginUserInfoHelper.getUserId();
    SysUser sysUser = userService.getById(userId);

    ProcessRecord processRecord = new ProcessRecord();
    processRecord.setProcessId(processId);
    processRecord.setStatus(status);
    processRecord.setDescription(description);
    processRecord.setOperateUser(sysUser.getName());
    processRecord.setOperateUserId(userId);
    this.save(processRecord);
  }
}
