package com.winmanboo.oh_my_oa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.model.system.SysMenu;
import com.winmanboo.model.system.SysRoleMenu;
import com.winmanboo.oh_my_oa.mapper.SysMenuMapper;
import com.winmanboo.oh_my_oa.service.SysMenuService;
import com.winmanboo.oh_my_oa.service.SysRoleMenuService;
import com.winmanboo.oh_my_oa.utils.MenuHelper;
import com.winmanboo.vo.system.AssignMenuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author winmanboo
 * @since 2023-03-31
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
  private final SysRoleMenuService sysRoleMenuService;

  @Override
  public List<SysMenu> findNodes() {
    // 查询所有的菜单数据
    List<SysMenu> sysMenuList = baseMapper.selectList(null);

    // 构建树形结构
    return MenuHelper.buildTree(sysMenuList);
  }

  @Override
  public void removeMenuById(Long id) {
    // 判断当前菜单是否有子菜单
    boolean hasChildrenMenu = lambdaQuery().eq(SysMenu::getParentId, id).count() > 0;
    if (hasChildrenMenu) {
      throw new OhMyOaException("该菜单有子菜单不能直接删除");
    }
    this.removeById(id);
  }

  @Override
  public List<SysMenu> findMenuByRoleId(Long roleId) {
    // 查询所有可用的菜单 -> status = 1
    List<SysMenu> availableMenuList = lambdaQuery().eq(SysMenu::getStatus, 1).list();

    // 根据角色 id 查询角色菜单关系表里面的角色 id 对应的所有的菜单 id
    List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.lambdaQuery().eq(SysRoleMenu::getRoleId, roleId).list();

    // 根据获取的菜单 id 获取对应的菜单
    List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).toList();
    availableMenuList.forEach(menu -> {
      // isSelect 表示当前角色已经分配的菜单
      menu.setSelect(menuIdList.contains(menu.getId()));
    });

    // 返回树形格式的菜单列表
    return MenuHelper.buildTree(availableMenuList);
  }

  @Transactional
  @Override
  public void doAssign(AssignMenuVo assignMenuVo) {
    // 根据角色 id 删除菜单关联关系
    sysRoleMenuService.lambdaUpdate().eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId()).remove();

    // 批量新增角色与菜单的关联关系
    List<SysRoleMenu> sysRoleMenuList = assignMenuVo.getMenuIdList().stream().map(menuId -> {
      SysRoleMenu sysRoleMenu = new SysRoleMenu();
      sysRoleMenu.setMenuId(menuId);
      sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
      return sysRoleMenu;
    }).toList();

    sysRoleMenuService.saveBatch(sysRoleMenuList);
  }
}
