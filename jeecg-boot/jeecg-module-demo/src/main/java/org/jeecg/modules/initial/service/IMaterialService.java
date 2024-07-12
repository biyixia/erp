package org.jeecg.modules.initial.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.initial.entity.Material;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysUser;

/**
 * @Description: 物料管理
 * @Author: jeecg-boot
 * @Date: 2023-04-07
 * @Version: V1.0
 */
public interface IMaterialService extends IService<Material> {
    /**
     * 用户组件数据查询
     */
    IPage<Material> queryMaterialPageList(String owningCode, String materialName, int pageSize, int pageNo, String id);
}
