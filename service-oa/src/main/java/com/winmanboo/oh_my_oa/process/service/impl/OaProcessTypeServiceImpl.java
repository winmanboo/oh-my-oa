package com.winmanboo.oh_my_oa.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.process.ProcessTemplate;
import com.winmanboo.model.process.ProcessType;
import com.winmanboo.oh_my_oa.process.mapper.OaProcessTypeMapper;
import com.winmanboo.oh_my_oa.process.service.OaProcessTemplateService;
import com.winmanboo.oh_my_oa.process.service.OaProcessTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
@Service
@RequiredArgsConstructor
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {
  private final OaProcessTemplateService processTemplateService;

  @Override
  public List<ProcessType> findProcessType() {
    // 1、查询所有审批分类，返回 list 集合
    // 2、遍历返回所有的审批分类
    return this.list().stream().peek(processType -> {
      // 3、得到每个审批分类，根据审批分类 id 查询对应审批模版
      List<ProcessTemplate> processTemplateList = processTemplateService.getByProcessTypeId(processType.getId());
      // 4、根据审批分类 id 查询对应审批模版数据（list）封装到每个审批分类对象里面
      processType.setProcessTemplateList(processTemplateList);
    }).collect(Collectors.toList());
  }
}
