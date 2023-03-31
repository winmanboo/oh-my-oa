package com.winmanboo.oh_my_oa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.model.system.SysMenu;
import com.winmanboo.oh_my_oa.mapper.SysMenuMapper;
import com.winmanboo.oh_my_oa.service.SysMenuService;
import com.winmanboo.oh_my_oa.utils.MenuHelper;
import org.springframework.stereotype.Service;

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
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

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
}
