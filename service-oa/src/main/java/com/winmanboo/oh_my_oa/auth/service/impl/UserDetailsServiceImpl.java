package com.winmanboo.oh_my_oa.auth.service.impl;

import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.model.system.SysUser;
import com.winmanboo.oh_my_oa.auth.service.SysMenuService;
import com.winmanboo.oh_my_oa.auth.service.SysUserService;
import com.winmanboo.security.domain.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final SysUserService sysUserService;

  private final SysMenuService sysMenuService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    SysUser sysUser = sysUserService.getUserByUsername(username);
    if (sysUser == null) {
      throw new UsernameNotFoundException("用户不存在！");
    }
    if (sysUser.getStatus() == 0) {
      throw new OhMyOaException("账号已停用，请联系管理员！");
    }

    // 根据用户 id 查询用户操作权限数据
    List<SimpleGrantedAuthority> grantedAuthority = sysMenuService.findUserPermsByUserId(sysUser.getId()).stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());;

    return new SecurityUser(sysUser, grantedAuthority);
  }
}
