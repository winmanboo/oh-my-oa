package com.winmanboo.oh_my_oa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winmanboo.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author winmanboo
 * @since 2023-03-31
 */
public interface SysMenuService extends IService<SysMenu> {

  List<SysMenu> findNodes();

  void removeMenuById(Long id);
}
