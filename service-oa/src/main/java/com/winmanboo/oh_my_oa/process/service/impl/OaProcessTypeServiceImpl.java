package com.winmanboo.oh_my_oa.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.process.ProcessType;
import com.winmanboo.oh_my_oa.process.mapper.OaProcessTypeMapper;
import com.winmanboo.oh_my_oa.process.service.OaProcessTypeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-04
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

}
