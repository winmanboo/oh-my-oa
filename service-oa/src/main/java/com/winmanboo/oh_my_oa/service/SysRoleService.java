package com.winmanboo.oh_my_oa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winmanboo.model.system.SysRole;
import com.winmanboo.vo.system.AssignRoleVo;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {

  /**
   * 查询所有角色和当前用户所属角色
   *
   * @param userId 用户id
   */
  Map<String, Object> findRoleDataByUserId(Long userId);

  /**
   * 为用户分配角色
   */
  void doAssign(AssignRoleVo assignRoleVo);
}
