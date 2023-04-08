package com.winmanboo.oh_my_oa.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.model.process.Process;
import com.winmanboo.model.process.ProcessRecord;
import com.winmanboo.model.process.ProcessTemplate;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.auth.service.SysUserService;
import com.winmanboo.oh_my_oa.process.mapper.OaProcessMapper;
import com.winmanboo.oh_my_oa.process.service.OaProcessRecordService;
import com.winmanboo.oh_my_oa.process.service.OaProcessService;
import com.winmanboo.oh_my_oa.process.service.OaProcessTemplateService;
import com.winmanboo.security.helper.LoginUserInfoHelper;
import com.winmanboo.vo.process.ApprovalVo;
import com.winmanboo.vo.process.ProcessFormVo;
import com.winmanboo.vo.process.ProcessQueryVo;
import com.winmanboo.vo.process.ProcessVo;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

  private final HistoryService historyService;

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
    List<ProcessVo> processVoList = new ArrayList<>();
    for (Task task : taskList) {
      String processInstanceId = task.getProcessInstanceId();
      ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
          .processInstanceId(processInstanceId)
          .singleResult();
      String businessKey = processInstance.getBusinessKey(); // processId
      if (!StringUtils.hasText(businessKey)) continue;
      Process process = this.getById(Long.parseLong(businessKey));
      if (process == null) continue;
      ProcessVo processVo = new ProcessVo();
      BeanUtils.copyProperties(process, processVo);
      processVo.setTaskId(task.getId());
      processVoList.add(processVo);
    }

    // 封装返回 page 对象
    IPage<ProcessVo> page = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
    page.setRecords(processVoList);
    return page;
  }

  @Override
  public Map<String, Object> show(Long processId) {
    // 根据流程 id 获取流程信息
    Process process = this.getById(processId);

    // 根据流程 id 获取流程记录信息
    List<ProcessRecord> processRecordList = processRecordService.lambdaQuery()
        .eq(ProcessRecord::getProcessId, processId)
        .list();

    // 根据模版 id 查询模版信息
    ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

    // 判断当前用户是否可以审批（可以看到信息的人不一定能审批，以及不能重复审批）
    List<Task> taskList = getCurrentTaskList(process.getProcessInstanceId());
    // 判断任务审批人是否是当前用户
    boolean isApprove = taskList.stream().allMatch(task -> task.getAssignee().equals(LoginUserInfoHelper.getUsername()));

    // 查询数据分装到 map 集合返回
    return Map.of(
        "process", process,
        "processRecordList", processRecordList,
        "processTemplate", processTemplate,
        "isApprove", isApprove
    );
  }

  @Override
  public void approve(ApprovalVo approvalVo) {
    // 从 approvalVo 获取任务 id，根据任务 id 获取流程变量
    String taskId = approvalVo.getTaskId();
    Map<String, Object> variables = taskService.getVariables(taskId);
    for (Map.Entry<String, Object> entry : variables.entrySet()) {
      System.out.println(entry.getKey());
      System.out.println(entry.getValue());
    }

    // 判断审批状态值
    // 状态值为 1 表示审批通过
    // 状态值为 -1 表示驳回，流程直接结束
    if (approvalVo.getStatus() == 1) {
      taskService.complete(taskId);
    } else {
      this.endTask(taskId);
    }

    // 记录审批相关过程信息 -> oa_process_record
    String desc = approvalVo.getStatus() == 1 ? "已通过" : "驳回";
    processRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), desc);

    // 查询下一个审批人，更新流程表中的记录 -> oa_process
    Process process = this.getById(approvalVo.getProcessId());
    // 查询任务
    List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
    if (!Collections.isEmpty(taskList)) {
      List<String> realNameList = taskList.stream().map(task -> {
        String assignee = task.getAssignee();
        SysUser user = userService.getUserByUsername(assignee);
        return user.getName();
      }).toList();
      // TODO: 2023/4/8 公共号的方式进行消息推送

      // 更新 process 流程信息
      process.setDescription("等待" + String.join(",", realNameList) + "审批");
      process.setStatus(1);
    } else {
      if (approvalVo.getStatus() == 1) { // 同意
        process.setDescription("审批完成（通过）");
        process.setStatus(2);
      } else {
        process.setDescription("审批完成（驳回）");
        process.setStatus(-1);
      }
    }
    this.updateById(process);
  }

  @Override
  public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
    // 封装查询条件
    HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
        .taskAssignee(LoginUserInfoHelper.getUsername())
        .finished() // 已经完成的任务
        .orderByTaskCreateTime()
        .desc();

    // 调用方法进行分页查询，返回 list 集合
    long begin = (pageParam.getCurrent() - 1) * pageParam.getSize();
    long size = pageParam.getSize();
    List<HistoricTaskInstance> list = query.listPage((int) begin, (int) size);
    long totalCount = query.count();

    List<ProcessVo> processVoList = new ArrayList<>();
    // 遍历集合，封装成 List<ProcessVo>
    for (HistoricTaskInstance item : list) {
      // 流程实例 id
      String processInstanceId = item.getProcessInstanceId();
      // 根据流程实例id查询获取 process 信息
      Process process = lambdaQuery().eq(Process::getProcessInstanceId, processInstanceId).one();
      ProcessVo processVo = new ProcessVo();
      BeanUtils.copyProperties(process, processVo);
      processVoList.add(processVo);
    }

    // IPage 封装分页查询所有数据并返回
    IPage<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
    pageModel.setRecords(processVoList);
    return pageModel;
  }

  /**
   * 结束流程
   * @param taskId 任务id
   */
  private void endTask(String taskId) {
    // 根据 taskId 获取任务对象
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

    // 获取流程定义模型 BpmnModel
    BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());

    // 获取结束的流向节点
    List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
    if (endEventList.isEmpty()) return;
    FlowNode endFlowNode = endEventList.get(0);

    // 获取当前的流向节点
    FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

    // 临时保存当前活动的原始方向（可选）
    List<SequenceFlow> originalSequenceFlowList = new ArrayList<>(currentFlowNode.getOutgoingFlows());

    // 清理当前流动方向
    currentFlowNode.getOutgoingFlows().clear();

    // 创建新的流向
    SequenceFlow sequenceFlow = new SequenceFlow();
    sequenceFlow.setId("newSequenceFlow");
    sequenceFlow.setSourceFlowElement(currentFlowNode);
    sequenceFlow.setTargetFlowElement(endFlowNode);

    // 当前节点指向新的方向
    List<SequenceFlow> sequenceFlowList = new ArrayList<>();
    sequenceFlowList.add(sequenceFlow);
    currentFlowNode.setOutgoingFlows(sequenceFlowList);

    // 完成当前任务
    taskService.complete(taskId);
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
