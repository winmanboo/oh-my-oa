package com.winmanboo.oh_my_oa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.system.SysRole;
import com.winmanboo.oh_my_oa.mapper.SysRoleMapper;
import com.winmanboo.oh_my_oa.service.SysRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
