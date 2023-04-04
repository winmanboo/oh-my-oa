package com.winmanboo.oh_my_oa.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ProcessTestDemo1 {
  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private HistoryService historyService;

  /* 任务分配 */

  // 1、固定分配
  // 前面的业务流程建模时指定固定的任务负责人，如：Assignee：zhangsan、lisi

  // 2、表达式分配
  // activiti 使用 UEL 表达式，UEL 是 javaEE6 规范的一部分，UEL 即统一表达式语言，activiti 支持两个 UEL 表达式：UEL-value 和 UEL-method

  // 以下是基于表达式分配中的 UEL-value 表达式
  // 首先在 activiti-explorer 中将 assignee 改成 ${assignee1} 这种表达式

  // 部署流程定义
  @Test
  public void deployProcess() {
    Deployment deployment = repositoryService.createDeployment()
        .addClasspathResource("process/jiaban.bpmn20.xml")
        .name("加班申请流程")
        .deploy();
    System.out.println(deployment.getId());
    System.out.println(deployment.getName());
  }


  // 启动流程实例
  @Test
  public void startProcessInstance() {
    Map<String, Object> map = new HashMap<>();
    // 设置任务人
    map.put("assignee1", "lucy");
    map.put("assignee2", "mary");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban", map);
    System.out.println(processInstance.getProcessDefinitionId());
    System.out.println(processInstance.getId());
  }

  // 查询某个人的代办任务
  @Test
  public void findTaskList() {
    String assign = "jack";
    List<Task> list = taskService.createTaskQuery()
        .taskAssignee(assign)
        .list();
    list.forEach(task -> {
      System.out.println("流程实例id：" + task.getProcessInstanceId());
      System.out.println("任务id：" + task.getId());
      System.out.println("任务负责人：" + task.getAssignee());
      System.out.println("任务名称：" + task.getName());
    });
  }

  // 以下是基于 UEL-value 表达式
  // 首先在 activiti-explorer 中将 assignee 改成 ${userBean.getUsername(1)} 这种表达式
  @Test
  public void deployProcess01() {
    Deployment deployment = repositoryService.createDeployment()
        .addClasspathResource("process/jiaban01.bpmn20.xml")
        .name("加班申请流程01")
        .deploy();
    System.out.println(deployment.getId());
    System.out.println(deployment.getName());
  }

  @Test
  public void startProcessInstance01() {
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban01");
    System.out.println(processInstance.getProcessDefinitionId());
    System.out.println(processInstance.getId());
  }

  // 3、监听器分配
  // 使用监听器的方式来指定负责人，那么在流程设计时就不需要指定 assignee
  // 任务监听器是发生对应的任务相关事件时执行自定义的 java 逻辑或表达式
  // Event 包含：
  // Create：任务创建后触发
  // Assignment：任务分配后触发
  // Delete：任务完成后触发
  // All：所有事件发生都触发

  @Test
  public void deployProcess02() {
    Deployment deployment = repositoryService.createDeployment()
        .addClasspathResource("process/jiaban02.bpmn20.xml")
        .name("加班申请流程02")
        .deploy();
    System.out.println(deployment.getId());
    System.out.println(deployment.getName());
  }

  @Test
  public void startProcessInstance02() {
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban02");
    System.out.println(processInstance.getProcessDefinitionId());
    System.out.println(processInstance.getId());
  }
}
