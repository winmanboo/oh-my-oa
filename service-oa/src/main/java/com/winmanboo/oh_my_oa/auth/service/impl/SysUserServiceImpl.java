package com.winmanboo.oh_my_oa.auth.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.auth.mapper.SysUserMapper;
import com.winmanboo.oh_my_oa.auth.service.SysUserService;
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

  @Override
  public void updateStatus(Long userId, Integer status) {
    /*SysUser sysUser = baseMapper.selectById(userId);
    sysUser.setStatus(status);
    baseMapper.updateById(sysUser);*/

    lambdaUpdate().eq(SysUser::getId, userId).set(SysUser::getStatus, status).update();
  }

  @Override
  public SysUser getUserByUsername(String username) {
    return lambdaQuery().eq(SysUser::getUsername, username).one();
  }
}
