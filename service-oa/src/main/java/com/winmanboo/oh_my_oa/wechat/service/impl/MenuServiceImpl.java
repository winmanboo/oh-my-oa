package com.winmanboo.oh_my_oa.wechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.model.wechat.Menu;
import com.winmanboo.oh_my_oa.wechat.mapper.MenuMapper;
import com.winmanboo.oh_my_oa.wechat.service.MenuService;
import com.winmanboo.vo.wechat.MenuVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-08
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

  @Override
  public List<MenuVo> findMenuInfo() {
    // 查询所有菜单集合
    List<Menu> menuList = this.list();

    // 查询所有一级菜单 -> parent_id = 0
    List<Menu> oneLevelMenuList = menuList.stream().filter(menu -> menu.getParentId() == 0).toList();

    // 遍历一级菜单集，得到每个一级菜单
    return oneLevelMenuList.stream().map(oneMenu -> {
      MenuVo menuVo = new MenuVo();
      BeanUtils.copyProperties(oneMenu, menuVo);
      // 获取每个一级菜单里面的所有二级菜单 id 和 parent_id 进行比较
      List<MenuVo> twoMenuList = menuList.stream().filter(menu -> Objects.equals(menu.getParentId(), oneMenu.getId()))
          .map(menu -> {
            // 把一级菜单里面所有的二级菜单获取到，封装一级菜单 children 集合里面
            MenuVo twoMenuVo = new MenuVo();
            BeanUtils.copyProperties(menu, twoMenuVo);
            return twoMenuVo;
          })
          .toList();
      menuVo.setChildren(twoMenuList);
      return menuVo;
    }).collect(Collectors.toList());
  }
}
