package com.winmanboo.oh_my_oa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.system.SysRole;
import com.winmanboo.model.system.SysUserRole;
import com.winmanboo.oh_my_oa.mapper.SysRoleMapper;
import com.winmanboo.oh_my_oa.service.SysRoleService;
import com.winmanboo.oh_my_oa.service.SysUserRoleService;
import com.winmanboo.vo.system.AssignRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
  private final SysUserRoleService sysUserRoleService;

  @Override
  public Map<String, Object> findRoleDataByUserId(Long userId) {
    // 所有的角色
    List<SysRole> allRoleList = baseMapper.selectList(null);

    // 获取用户当前已分配的角色关联关系
    List<SysUserRole> existUserRoleIdList = sysUserRoleService.lambdaQuery().eq(SysUserRole::getUserId, userId).list();

    // 从查询出来的用户 id 对应角色 list 集合，获取所有角色 id
    List<Long> existRoleIdList = existUserRoleIdList.stream().map(SysUserRole::getRoleId).toList(); // since 16

    // 从所有的角色中过滤出当前用户所关联的所有角色
    List<SysRole> assignRoleList = allRoleList.stream().filter(sysRole -> existRoleIdList.contains(sysRole.getId())).toList();

    return Map.of(
        "assignRoleList", assignRoleList,
        "allRolesList", allRoleList
    );
  }

  @Transactional
  @Override
  public void doAssign(AssignRoleVo assignRoleVo) {
    // 把用户之前分配的角色数据删除
    sysUserRoleService.lambdaUpdate().eq(SysUserRole::getUserId, assignRoleVo.getUserId()).remove();

    // 然后重新进行分配
    List<SysUserRole> sysUserRoleList = assignRoleVo.getRoleIdList().stream().map(roleId -> {
      SysUserRole sysUserRole = new SysUserRole();
      sysUserRole.setUserId(assignRoleVo.getUserId());
      sysUserRole.setRoleId(roleId);
      return sysUserRole;
    }).toList();
    sysUserRoleService.saveBatch(sysUserRoleList);
  }
}
