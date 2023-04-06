package com.winmanboo.oh_my_oa.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.process.ProcessTemplate;
import com.winmanboo.oh_my_oa.process.mapper.OaProcessTemplateMapper;
import com.winmanboo.oh_my_oa.process.service.OaProcessService;
import com.winmanboo.oh_my_oa.process.service.OaProcessTemplateService;
import com.winmanboo.oh_my_oa.process.service.OaProcessTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
@Service
@RequiredArgsConstructor
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {
  private final OaProcessTypeService processTypeService;

  private final OaProcessService processService;

  @Override
  public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam) {
    /*// 调用 mapper 方法进行分页查询
    Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);

    // 从分页数据中获取列表集合
    List<ProcessTemplate> records = processTemplatePage.getRecords();

    // 遍历集合得到每个对象的审批类型 id
    for (ProcessTemplate record : records) {
      Long processTypeId = record.getProcessTypeId();
      // 根据审批类型 id，查询获取对应名称
      ProcessType processType = processTypeService.getById(processTypeId);
      if (processType == null) {
        continue;
      }
      // 最后进行封装返回
      record.setProcessTypeName(processType.getName());
    }

    processTemplatePage.setRecords(records);
    return processTemplatePage;*/

    return baseMapper.selectPageProcessTemplate(pageParam);
  }

  @Override
  public void publish(Long id) {
    ProcessTemplate processTemplate = this.getById(id);
    processTemplate.setStatus(1);
    this.updateById(processTemplate);

    // 流程定义部署
    if (StringUtils.hasText(processTemplate.getProcessDefinitionPath())) {
      processService.deployByZip(processTemplate.getProcessDefinitionPath());
    }
  }

  @Override
  public List<ProcessTemplate> getByProcessTypeId(Long id) {
    return lambdaQuery().eq(ProcessTemplate::getProcessTypeId, id).list();
  }
}
