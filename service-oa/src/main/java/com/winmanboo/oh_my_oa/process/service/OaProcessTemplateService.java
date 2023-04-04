package com.winmanboo.oh_my_oa.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winmanboo.model.process.ProcessTemplate;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {

  IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam);
}
