package com.winmanboo.oh_my_oa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
}
