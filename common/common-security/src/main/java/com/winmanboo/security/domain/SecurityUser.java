package com.winmanboo.security.domain;

import com.winmanboo.model.system.SysUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class SecurityUser extends User {
  private SysUser sysUser;

  public SecurityUser(SysUser sysUser, Collection<? extends GrantedAuthority> authorities) {
    super(sysUser.getUsername(), sysUser.getPassword(), authorities);
    this.sysUser = sysUser;
  }
}
