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
public class ProcessTestDemo2 {
  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private HistoryService historyService;


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
    map.put("assignee1", "lucy02");
    map.put("assignee2", "mary02");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban", map);
    System.out.println(processInstance.getProcessDefinitionId());
    System.out.println(processInstance.getId());
  }

  // 查询某个人的代办任务
  @Test
  public void findTaskList() {
    String assign = "lucy02";
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

}
