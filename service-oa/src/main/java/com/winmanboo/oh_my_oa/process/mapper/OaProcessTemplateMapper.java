package com.winmanboo.oh_my_oa.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winmanboo.model.process.ProcessTemplate;

/**
 * <p>
 * 审批模板 Mapper 接口
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
public interface OaProcessTemplateMapper extends BaseMapper<ProcessTemplate> {

  IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam);
}
