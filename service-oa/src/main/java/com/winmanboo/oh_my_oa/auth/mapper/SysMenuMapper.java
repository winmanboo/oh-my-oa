package com.winmanboo.oh_my_oa.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winmanboo.model.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author winmanboo
 * @since 2023-03-31
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

  List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}
