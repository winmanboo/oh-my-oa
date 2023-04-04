package com.winmanboo.oh_my_oa.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winmanboo.model.process.Process;
import com.winmanboo.vo.process.ProcessQueryVo;
import com.winmanboo.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
public interface OaProcessMapper extends BaseMapper<Process> {

  IPage<ProcessVo> selectPageVo(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo);
}
