package com.winmanboo.oh_my_oa.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.model.process.Process;
import com.winmanboo.oh_my_oa.process.mapper.OaProcessMapper;
import com.winmanboo.oh_my_oa.process.service.OaProcessService;
import com.winmanboo.vo.process.ProcessQueryVo;
import com.winmanboo.vo.process.ProcessVo;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.stereotype.Service;

import java.io.InputStream;
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
}
