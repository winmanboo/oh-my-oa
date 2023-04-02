package com.winmanboo.oh_my_oa.service.impl;

import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.service.SysUserService;
import com.winmanboo.security.domain.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final SysUserService sysUserService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    SysUser sysUser = sysUserService.getUserByUsername(username);
    if (sysUser == null) {
      throw new UsernameNotFoundException("用户不存在！");
    }
    if (sysUser.getStatus() == 0) {
      throw new OhMyOaException("账号已停用，请联系管理员！");
    }
    return new SecurityUser(sysUser, Collections.emptyList());
  }
}
