package com.winmanboo.oh_my_oa.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProcessTest {

  // 注入 RepositoryService：Activiti 的资源管理类，该服务负责部署流程定义，管理流程资源
  @Autowired
  private RepositoryService repositoryService;

  // Activiti 的流程运行管理类，用于开始一个新的流程实例，获取关于流程执行的相关信息
  @Autowired
  private RuntimeService runtimeService;

  // Activiti 的任务管理类，用于处理业务运行中的各种任务，例如查询分给用户或组的任务、创建新的任务、分配任务、确定和完成一个任务
  @Autowired
  private TaskService taskService;

  @Autowired
  private HistoryService historyService;

  // 单个文件部署方式
  @Test
  public void deployProcess() {
    // 流程部署
    Deployment deploy = repositoryService.createDeployment()
        .addClasspathResource("process/qingjia.bpmn20.xml")
        .addClasspathResource("process/qingjia.png")
        .name("请假申请流程")
        .deploy();
    System.out.println(deploy.getId());
    System.out.println(deploy.getName());
  }

  // 启动流程实例
  @Test
  public void startProcess() {
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia");
    System.out.println("流程定义id：" + processInstance.getProcessInstanceId());
    System.out.println("流程实例id：" + processInstance.getId());
    System.out.println("流程活动id：" + processInstance.getActivityId());
  }

  // 查询某个人的代办任务——zhangsan
  @Test
  public void findTaskList() {
    String assign = "zhangsan";
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

  // 处理当前任务
  @Test
  public void completeTask() {
    // 查询负责人需要处理的任务，返回一条
    Task task = taskService.createTaskQuery()
        .taskAssignee("zhangsan")
        .singleResult();
    // 完成任务，参数为任务 id
    taskService.complete(task.getId());
    // 完成后会自动到下一个节点
  }

  // 查询已经处理的任务
  @Test
  public void findCompleteTask() {
    List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
        .taskAssignee("zhangsan")
        .finished()
        .list();
    list.forEach(instance -> {
      System.out.println("实例id：" + instance.getProcessInstanceId());
      System.out.println("任务id：" + instance.getId());
      System.out.println("任务负责人：" + instance.getAssignee());
      System.out.println("任务名称：" + instance.getName());
    });
  }

  // 让实际业务与 activiti 表关联（BusinessKey）业务key
  // BusinessKey：业务标识，通常为业务的主键，业务标识和流程标识一一对应，业务标识来源于业务系统，
  // 存储业务标识就是根据业务标识来关联查询业务系统的数据
  // 举例：请假流程启动一个流程实例，就可以将请假单的 id 作为业务标识存储到 activiti 中，将来查询 activiti 的流程实例信息，
  // 就可以获取请假单的 id 从而关联查询业务系统数据库得到请假单信息
  // 启动流程实例，添加 BusinessKey
  @Test
  public void startUpProcessAddBusinessKey() {
    String businessKey = "1";
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", businessKey);
    // 输出
    System.out.println("业务id：" + processInstance.getBusinessKey());
  }

  /* 挂起、激活实例 */
  // 某些情况下由于流程变更需要将当前运行的流程暂停而不是直接删除，流程暂停后将不会执行

  // 全部流程实例挂起
  @Test
  public void suspendProcessInstanceAll() {
    // 获取流程定义的对象
    ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery()
        .processDefinitionKey("qingjia")
        .singleResult();
    // 调用流程定义对象的方法判断当前状态：挂起、激活
    boolean suspended = qingjia.isSuspended();

    // 判断如果是挂起，就激活
    if (suspended) {
      // 第一个参数：流程定义id
      // 第二个参数：是否激活
      // 第三个参数：时间点
      repositoryService.activateProcessDefinitionById(qingjia.getId(), true, null);
      System.out.println(qingjia.getId() + " 激活了");
    } else {
      // 如果是激活，就挂起
      repositoryService.suspendProcessDefinitionById(qingjia.getId(), true, null);
      System.out.println(qingjia.getId() + " 挂起了");
    }
  }

  // 单个流程实例挂起
  @Test
  public void singleSuspendProcessInstance() {
    // act_ru_execution -> proc_inst_id（流程实例id）
    String processInstanceId = "693a5208-d236-11ed-ac7b-120c7f5212e2";
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId)
        .singleResult();
    boolean suspended = processInstance.isSuspended();
    if (suspended) {
      runtimeService.activateProcessInstanceById(processInstanceId);
      System.out.println("流程实例：" + processInstanceId + "激活");
    } else {
      runtimeService.suspendProcessInstanceById(processInstanceId);
      System.out.println("流程实例：" + processInstanceId + "挂起");
    }
  }



  
}
