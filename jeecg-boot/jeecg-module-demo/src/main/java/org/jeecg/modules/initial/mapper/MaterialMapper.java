package org.jeecg.modules.initial.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.initial.entity.Material;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.system.entity.SysUser;

/**
 * @Description: 物料管理
 * @Author: jeecg-boot
 * @Date:   2023-04-07
 * @Version: V1.0
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
    /**
     * 根据部门查询部门用户
     * @param page
     * @return
     */
    IPage<Material> queryMaterialPageList(Page<Material> page, @Param("owningCode") String owningCode, @Param("materialName") String materialName);
}
