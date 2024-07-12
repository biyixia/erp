package org.jeecg.modules.initial.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.initial.entity.Material;
import org.jeecg.modules.initial.entity.MaterialType;
import org.jeecg.modules.initial.mapper.MaterialMapper;
import org.jeecg.modules.initial.service.IMaterialService;
import org.jeecg.modules.initial.service.IMaterialTypeService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 物料管理
 * @Author: jeecg-boot
 * @Date:   2023-04-07
 * @Version: V1.0
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements IMaterialService {

    @Autowired
    IMaterialTypeService materialTypeService;

    @Autowired
    MaterialMapper materialMapper;

    @Override
    public IPage<Material> queryMaterialPageList(String owningCode, String materialName, int pageSize, int pageNo, String id) {
        IPage<Material> pageList = null;
        // 物料类型ID不存在 直接查询用户表即可
        Page<Material> page = new Page<>(pageNo, pageSize);
        if(oConvertUtils.isEmpty(owningCode)){
            LambdaQueryWrapper<Material> query = new LambdaQueryWrapper<>();
            if(oConvertUtils.isNotEmpty(materialName)){
                query.like(Material::getMaterialName, materialName);
            }
            if(oConvertUtils.isNotEmpty(id)){
                query.eq(Material::getId, id);
            }
            pageList = materialMapper.selectPage(page, query);
        }else{
            // 有物料类型ID 需要走自定义sql
            pageList = this.baseMapper.queryMaterialPageList(page, owningCode,materialName);
        }
//        List<Material> materialList = pageList.getRecords();
//        if(materialList!=null && materialList.size()>0){
//            List<String> materialIds = materialList.stream().map(Material::getId).collect(Collectors.toList());
//            Map<String, Material> map = new HashMap(5);
//            if(materialIds!=null && materialIds.size()>0){
//                // 查部门名称
//                Map<String,String>  useDepNames = this.getDepNamesByUserIds(materialIds);
//                materialList.forEach(item->{
//                    //TDO 临时借用这个字段用于页面展示
//                    item.setOrgCodeTxt(useDepNames.get(item.getId()));
//                    item.setSalt("");
//                    item.setPassword("");
//                    // 去重
//                    map.put(item.getId(), item);
//                });
//            }
//            pageList.setRecords(new ArrayList<>(map.values()));
//        }
        return pageList;
    }
}
