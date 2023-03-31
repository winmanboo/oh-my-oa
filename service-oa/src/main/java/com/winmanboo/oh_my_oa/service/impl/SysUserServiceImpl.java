package com.winmanboo.oh_my_oa.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.mapper.SysUserMapper;
import com.winmanboo.oh_my_oa.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author winmanboo
 * @since 2023-03-31
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

}
