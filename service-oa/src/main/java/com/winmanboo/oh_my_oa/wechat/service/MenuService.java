package com.winmanboo.oh_my_oa.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winmanboo.model.wechat.Menu;
import com.winmanboo.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author winmanboo
 * @since 2023-04-08
 */
public interface MenuService extends IService<Menu> {

  List<MenuVo> findMenuInfo();
}
