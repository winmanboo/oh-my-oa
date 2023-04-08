package com.winmanboo.oh_my_oa.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.winmanboo.model.system.SysUser;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author winmanboo
 * @since 2023-03-31
 */
public interface SysUserService extends IService<SysUser> {

  void updateStatus(Long userId, Integer status);

  SysUser getUserByUsername(String username);

  Map<String, Object> getCurrentUser();
}
