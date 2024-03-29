package com.winmanboo.oh_my_oa.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winmanboo.model.system.SysMenu;
import com.winmanboo.vo.system.AssignMenuVo;
import com.winmanboo.vo.system.RouterVo;

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

  List<SysMenu> findMenuByRoleId(Long roleId);

  void doAssign(AssignMenuVo assignMenuVo);

  List<RouterVo> findUserMenuListByUserId(Long userId);

  List<String> findUserPermsByUserId(Long userId);
}
