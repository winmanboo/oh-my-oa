package com.winmanboo.oh_my_oa.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.model.process.Process;
import com.winmanboo.model.process.ProcessTemplate;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.auth.service.SysUserService;
import com.winmanboo.oh_my_oa.process.mapper.OaProcessMapper;
import com.winmanboo.oh_my_oa.process.service.OaProcessRecordService;
import com.winmanboo.oh_my_oa.process.service.OaProcessService;
import com.winmanboo.oh_my_oa.process.service.OaProcessTemplateService;
import com.winmanboo.security.helper.LoginUserInfoHelper;
import com.winmanboo.vo.process.ProcessFormVo;
import com.winmanboo.vo.process.ProcessQueryVo;
import com.winmanboo.vo.process.ProcessVo;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

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
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {
  private final RepositoryService repositoryService;

  private final SysUserService userService;

  private final OaProcessTemplateService processTemplateService;

  private final RuntimeService runtimeService;

  private final TaskService taskService;

  private final OaProcessRecordService processRecordService;

  @Override
  public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
    return baseMapper.selectPageVo(pageParam, processQueryVo);
  }

  @Override
  public void deployByZip(String deployPath) {
    InputStream input = this.getClass().getClassLoader().getResourceAsStream(deployPath);
    if (input == null)
      throw new OhMyOaException("not found file from " + deployPath);
    ZipInputStream zipInputStream = new ZipInputStream(input);
    // 流程部署
    Deployment deployment = repositoryService.createDeployment()
        .addZipInputStream(zipInputStream)
        .deploy();
    System.out.println(deployment.getId());
    System.out.println(deployment.getName());
  }

  @Transactional
  @Override
  public void startUp(ProcessFormVo processFormVo) {
    // 根据当前用户 id 获取用户信息
    Long userId = LoginUserInfoHelper.getUserId();
    SysUser sysUser = userService.getById(userId);

    // 根据审批模版 id 查询模版信息
    ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());

    // 保存提交的手机信息到业务表 -> oa_process
    Process process = new Process();
    BeanUtils.copyProperties(processFormVo, process);
    process.setStatus(1);
    process.setProcessCode(String.valueOf(System.currentTimeMillis()));
    process.setUserId(userId);
    String formValues = processFormVo.getFormValues();
    process.setFormValues(formValues);
    process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
    this.save(process);

    // 启动流程实例 -> RuntimeService
    // 流程定义 key
    String processDefinitionKey = processTemplate.getProcessDefinitionKey();
    // 业务 key -> processId
    Long businessKey = process.getId();
    // 流程参数 -> form 表单 json 数据 转换 map 集合
    JSONObject jsonObject = JSON.parseObject(formValues);
    JSONObject formData = jsonObject.getJSONObject("formData");
    // 遍历 formData 得到内容，封装 map 集合
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,
        businessKey.toString(), Map.of("data", formData));

    // 查询下一个审批人 -> TaskService
    // 审批人可能有多个
    List<Task> taskList = this.getCurrentTaskList(processInstance.getId());
    List<String> realNameList = taskList.stream().map(task -> {
      String assignee = task.getAssignee(); // 用户名
      SysUser user = userService.getUserByUsername(assignee);// 根据用户名获取用户信息
      return user.getName(); // 返回真实名称
    }).toList();

    // TODO 推送消息

    // 业务和流程关联
    process.setProcessInstanceId(processInstance.getId());
    process.setDescription("等待" + String.join(",", realNameList) + "审批");
    this.updateById(process);

    // 记录操作审批信息记录
    processRecordService.record(process.getId(), 1, "发起申请");
  }

  @Override
  public IPage<ProcessVo> findPending(Page<Process> pageParam) {
    // 封装查询的条件，根据当前登陆的用户名
    TaskQuery query = taskService.createTaskQuery()
        .taskAssignee(LoginUserInfoHelper.getUsername())
        .orderByTaskCreateTime()
        .desc();

    // 调用方法，进行分页条件查询，返回代办任务集合
    long begin = (pageParam.getCurrent() - 1) * pageParam.getSize();
    long size = pageParam.getSize();
    List<Task> taskList = query.listPage((int) begin, (int) size);
    long totalCount = query.count();

    // 封装返回 list 集合数据到 List<ProcessVo> 中
    List<ProcessVo> processVoList = taskList.stream().filter(task -> StringUtils.hasText(task.getBusinessKey()))
        .map(task -> {
          /*String processInstanceId = task.getProcessInstanceId();
          ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
              .processInstanceId(processInstanceId)
              .singleResult();
          String businessKey = processInstance.getBusinessKey(); // processId*/
          String businessKey = task.getBusinessKey();
          Process process = this.getById(Long.parseLong(businessKey));
          ProcessVo processVo = new ProcessVo();
          BeanUtils.copyProperties(process, process);
          processVo.setTaskId(task.getId());
          return processVo;
        }).collect(Collectors.toList());

    // 封装返回 page 对象
    IPage<ProcessVo> page = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
    page.setRecords(processVoList);
    return page;
  }

  /**
   * 当前任务列表
   *
   * @param processId 流程实例id
   */
  private List<Task> getCurrentTaskList(String processId) {
    return taskService.createTaskQuery().processInstanceId(processId).list();
  }
}
