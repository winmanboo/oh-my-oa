package com.winmanboo.oh_my_oa.auth.utils;

import com.winmanboo.model.system.SysMenu;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class MenuHelper {
  /**
   * 用递归方式构建菜单树形结构
   *
   * @param sysMenuList 所有菜单
   */
  public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
    List<SysMenu> trees = new ArrayList<>();
    // 把所有的菜单数据进行遍历
    sysMenuList.forEach(sysMenu -> {
      if (sysMenu.getParentId() == 0) {
        trees.add(getChildren(sysMenu, sysMenuList));
      }
    });
    return trees;
  }

  private static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
    List<SysMenu> children = sysMenuList.stream().filter(item -> {
      if (Objects.equals(sysMenu.getId(), item.getParentId())) {
        getChildren(item, sysMenuList);
        return true;
      }
      return false;
    }).toList();
    sysMenu.setChildren(children);
    return sysMenu;
  }
}
