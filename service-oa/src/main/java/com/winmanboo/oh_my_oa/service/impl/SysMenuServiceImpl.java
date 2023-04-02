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
import com.winmanboo.vo.system.MetaVo;
import com.winmanboo.vo.system.RouterVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
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

  @Override
  public List<RouterVo> findUserMenuListByUserId(Long userId) {
    List<SysMenu> sysMenuList = null;
    // 如果用户是管理员，查询所有的菜单列表 userId = 1
    if (userId == 1) { // 是管理员
      sysMenuList = lambdaQuery().eq(SysMenu::getStatus, 1).orderByAsc(SysMenu::getSortValue).list();
    } else {
      // 如果不是管理员，根据 userId 查询可以操作的菜单列表
      // 多表关联查询：用户角色关系表、角色菜单关系表、菜单表
      sysMenuList = baseMapper.findMenuListByUserId(userId);
    }
    // 把查询出来的数据列表构建成 vue 框架要求的路由结构
    List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList); // 先构建出树形结构
    return this.buildRouter(sysMenuTreeList);
  }

  /**
   * 根据菜单构建路由
   *
   * @param menus 树形菜单
   * @return 返回前端所需的路由结构
   */
  private List<RouterVo> buildRouter(List<SysMenu> menus) {
    List<RouterVo> routers = new LinkedList<>();
    for (SysMenu menu : menus) {
      RouterVo router = new RouterVo();
      router.setHidden(false);
      router.setAlwaysShow(false);
      router.setPath(getRouterPath(menu));
      router.setComponent(menu.getComponent());
      router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
      List<SysMenu> children = menu.getChildren();
      //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
      if(menu.getType() == 1) {
        List<SysMenu> hiddenMenuList = children.stream().filter(item -> StringUtils.hasText(item.getComponent())).toList();
        for (SysMenu hiddenMenu : hiddenMenuList) {
          RouterVo hiddenRouter = new RouterVo();
          hiddenRouter.setHidden(true);
          hiddenRouter.setAlwaysShow(false);
          hiddenRouter.setPath(getRouterPath(hiddenMenu));
          hiddenRouter.setComponent(hiddenMenu.getComponent());
          hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
          routers.add(hiddenRouter);
        }
      } else {
        if (!CollectionUtils.isEmpty(children)) {
          if(children.size() > 0) {
            router.setAlwaysShow(true);
          }
          router.setChildren(buildRouter(children));
        }
      }
      routers.add(router);
    }
    return routers;
    /*return menus.stream().map(menu -> {
      RouterVo router = new RouterVo();
      router.setHidden(false);
      router.setAlwaysShow(false);
      router.setPath(getRouterPath(menu));
      router.setComponent(menu.getComponent());
      router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));

      List<SysMenu> children = menu.getChildren();
      if (menu.getType() == 1) { // 菜单
        // 加载隐藏路由
        List<SysMenu> hiddenMenuList = children.stream().filter(item -> StringUtils.hasText(item.getComponent())).toList();
        List<RouterVo> hiddenRouters = hiddenMenuList.stream().map(hiddenMenu -> {
          RouterVo hiddenRouter = new RouterVo();
          hiddenRouter.setHidden(true);
          hiddenRouter.setAlwaysShow(false);
          hiddenRouter.setPath(getRouterPath(hiddenMenu));
          hiddenRouter.setComponent(hiddenMenu.getComponent());
          hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
          return hiddenRouter;
        }).toList();
        router.setChildren(hiddenRouters);
      } else {
        if (!CollectionUtils.isEmpty(children)) {
          if (!children.isEmpty()) {
            router.setAlwaysShow(true);
          }
          router.setChildren(buildRouter(children));
        }
      }
      return router;
    }).toList();*/
  }

  /**
   * 获取路由地址
   *
   * @param menu 菜单
   * @return 路由地址，顶层地址前加'/'，非顶层不加'/'
   */
  private String getRouterPath(SysMenu menu) {
    String routerPath = "/" + menu.getPath();
    if (menu.getParentId() != 0) {
      routerPath = menu.getPath();
    }
    return routerPath;
  }

  @Override
  public List<String> findUserPermsByUserId(Long userId) {
    List<SysMenu> sysMenuList = null;
    // 判断是否是管理员，如果是管理员，查询所有的按钮列表
    if (userId == 1) {
      sysMenuList = lambdaQuery().eq(SysMenu::getStatus, 1).list();
    } else {
      // 如果不是管理员，根据 userId 查询可以操作的按钮列表
      sysMenuList = baseMapper.findMenuListByUserId(userId);
    }

    // 从查询出来的数据里面，获取可以操作按钮值的 list 集合，并返回
    return sysMenuList.stream().filter(menu -> menu.getType() == 2).map(SysMenu::getPerms).toList();
  }
}
