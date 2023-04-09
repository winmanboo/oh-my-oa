package com.winmanboo.oh_my_oa.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winmanboo.common.exception.OhMyOaException;
import com.winmanboo.model.wechat.Menu;
import com.winmanboo.oh_my_oa.wechat.mapper.MenuMapper;
import com.winmanboo.oh_my_oa.wechat.service.MenuService;
import com.winmanboo.vo.wechat.MenuVo;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
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
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
  private final WxMpService wxMpService;

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

  @Override
  public void syncMenu() {
    // 菜单数据查询出来，封装微信要求的菜单格式
    List<MenuVo> menuVoList = this.findMenuInfo();
    JSONArray buttonList = new JSONArray();
    for (MenuVo oneMenuVo : menuVoList) {
      JSONObject one = new JSONObject();
      one.put("name", oneMenuVo.getName());
      if (Collections.isEmpty(oneMenuVo.getChildren())) {
        one.put("type", oneMenuVo.getType());
        one.put("url", "http://452c25a4.r2.cpolar.cn/#" + oneMenuVo.getUrl());
      } else {
        JSONArray subButton = new JSONArray();
        for (MenuVo twoMenuVo : oneMenuVo.getChildren()) {
          JSONObject view = new JSONObject();
          view.put("type", twoMenuVo.getType());
          if (twoMenuVo.getType().equals("view")) {
            view.put("name", twoMenuVo.getName());
            // H5 页面地址
            view.put("url", "http://452c25a4.r2.cpolar.cn#" + twoMenuVo.getUrl());
          } else {
            view.put("name", twoMenuVo.getName());
            view.put("key", twoMenuVo.getMeunKey());
          }
          subButton.add(view);
        }
        one.put("sub_button", subButton);
      }
      buttonList.add(one);
    }
    // 菜单
    JSONObject button = new JSONObject();
    button.put("button", buttonList);
    try {
      // 调用工具里面的方法实现菜单推送
      wxMpService.getMenuService().menuCreate(button.toJSONString());
    } catch (WxErrorException e) {
      throw new OhMyOaException("推送失败");
    }
  }

  @SneakyThrows
  @Override
  public void removeMenu() {
    wxMpService.getMenuService().menuDelete();
  }
}
