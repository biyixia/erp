package org.jeecg.modules.initial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.initial.entity.MaterialType;
import org.jeecg.modules.initial.entity.MaterialTypeTreeModel;
import org.jeecg.modules.initial.mapper.MaterialTypeMapper;
import org.jeecg.modules.initial.service.IMaterialTypeService;
import org.jeecg.modules.initial.util.FindsChildrenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 物料类型
 * @Author: jeecg-boot
 * @Date: 2023-04-06
 * @Version: V1.0
 */
@Service
public class MaterialTypeServiceImpl extends ServiceImpl<MaterialTypeMapper, MaterialType> implements IMaterialTypeService {

    @Autowired
    MaterialTypeMapper materialTypeMapper;

    @Override
    public void getAllChildIds(MaterialType materialType,List<String> ids) {
        if ("1".equals(materialType.getHasChild())) {
            QueryWrapper<MaterialType> wrapper = new QueryWrapper<>();
            wrapper.eq("pid", materialType.getId());
            List<MaterialType> materialTypes = materialTypeMapper.selectList(wrapper);
            for (MaterialType child : materialTypes) {
                ids.add(child.getId());
                getAllChildIds(child,ids);
            }
        }
    }

    /**
     * 根据parentId查询部门树
     *
     * @param parentId
     * @param ids        前端回显传递
     * @param primaryKey 主键字段（id或者orgCode）
     * @return
     */
    @Override
    public List<MaterialTypeTreeModel> queryTreeListByPid(String parentId, String ids, String primaryKey) {
        Consumer<LambdaQueryWrapper<MaterialType>> square = i -> {
            if (oConvertUtils.isNotEmpty(ids)) {
//                if (CommonConstant.DEPART_KEY_ORG_CODE.equals(primaryKey)) {
//                    i.in(MaterialType::getOrgCode, ids.split(SymbolConstant.COMMA));
//                } else {
//                    i.in(MaterialType::getId, ids.split(SymbolConstant.COMMA));
//                }
            } else {
                if (oConvertUtils.isEmpty(parentId)) {
                    i.and(q -> q.isNull(true, MaterialType::getPid).or().eq(true, MaterialType::getPid, "0"));
                } else {
                    //查找所有的以及物料类型
                    i.eq(true, MaterialType::getPid, parentId);
                }
            }
        };
        LambdaQueryWrapper<MaterialType> lqw = new LambdaQueryWrapper<>();
        lqw.eq(true, MaterialType::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        lqw.func(square);
        List<MaterialType> list = list(lqw);
        List<MaterialTypeTreeModel> records = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MaterialType depart = list.get(i);
            MaterialTypeTreeModel treeModel = new MaterialTypeTreeModel(depart);
            //TDO 异步树加载key拼接__+时间戳,以便于每次展开节点会刷新数据
            //treeModel.setKey(treeModel.getKey()+"__"+System.currentTimeMillis());
            treeModel.setKey(treeModel.getKey());
            Integer count = this.baseMapper.queryCountByPid(depart.getId());
            if (count > 0) {
                treeModel.setIsLeaf(false);
            } else {
                treeModel.setIsLeaf(true);
            }
            records.add(treeModel);
        }
        return records;
    }

    /**
     * queryTreeList 根据物料类型id查询,前端回显调用
     */
    @Override
    public List<MaterialTypeTreeModel> queryTreeList(String ids) {
        List<MaterialTypeTreeModel> listResult = new ArrayList<>();
        LambdaQueryWrapper<MaterialType> query = new LambdaQueryWrapper<>();
        query.eq(MaterialType::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        if (oConvertUtils.isNotEmpty(ids)) {
            query.in(true, MaterialType::getId, ids.split(","));
        }
        List<MaterialType> list = this.list(query);
        for (MaterialType materialType : list) {
            listResult.add(new MaterialTypeTreeModel(materialType));
        }
        System.out.println(listResult);
        return listResult;
    }

    /**
     * 查询所有的物料类型数据,以树结构形式响应给前端
     */
    @Override
//    @Cacheable(value = CacheConstant.SYS_DEPARTS_CACHE)
    public List<MaterialTypeTreeModel> queryTreeList() {
        LambdaQueryWrapper<MaterialType> query = new LambdaQueryWrapper<>();
        query.eq(MaterialType::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        List<MaterialType> list = this.list(query);
        List<MaterialTypeTreeModel> listResult = FindsChildrenUtil.wrapTreeDataToTreeList(list);
        return listResult;
    }

    @Override
    public void addMaterialType(MaterialType materialType) {
        //新增时设置hasChild为0
        materialType.setHasChild(IMaterialTypeService.NOCHILD);
        if (oConvertUtils.isEmpty(materialType.getPid())) {
            materialType.setPid(IMaterialTypeService.ROOT_PID_VALUE);
        } else {
            //如果当前节点父ID不为空 则设置父节点的hasChildren 为1
            MaterialType parent = baseMapper.selectById(materialType.getPid());
            if (parent != null && !"1".equals(parent.getHasChild())) {
                parent.setHasChild("1");
                baseMapper.updateById(parent);
            }
        }
        baseMapper.insert(materialType);
    }

    @Override
    public void updateMaterialType(MaterialType materialType) {
        MaterialType entity = this.getById(materialType.getId());
        if (entity == null) {
            throw new JeecgBootException("未找到对应实体");
        }
        String old_pid = entity.getPid();
        String new_pid = materialType.getPid();
        if (!old_pid.equals(new_pid)) {
            updateOldParentNode(old_pid);
            if (oConvertUtils.isEmpty(new_pid)) {
                materialType.setPid(IMaterialTypeService.ROOT_PID_VALUE);
            }
            if (!IMaterialTypeService.ROOT_PID_VALUE.equals(materialType.getPid())) {
                baseMapper.updateTreeNodeStatus(materialType.getPid(), IMaterialTypeService.HASCHILD);
            }
        }
        baseMapper.updateById(materialType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterialType(String id) throws JeecgBootException {
        //查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if (id.indexOf(",") > 0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if (idVal != null) {
                    MaterialType materialType = this.getById(idVal);
                    String pidVal = materialType.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<MaterialType> dataList = baseMapper.selectList(new QueryWrapper<MaterialType>().eq("pid", pidVal).notIn("id", Arrays.asList(idArr)));
                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
                    if (flag) {
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for (String pid : pidArr) {
                this.updateOldParentNode(pid);
            }
        } else {
            MaterialType materialType = this.getById(id);
            if (materialType == null) {
                throw new JeecgBootException("未找到对应实体");
            }
            updateOldParentNode(materialType.getPid());
            baseMapper.deleteById(id);
        }
    }

    @Override
    public List<MaterialType> queryTreeListNoPage(QueryWrapper<MaterialType> queryWrapper) {
        List<MaterialType> dataList = baseMapper.selectList(queryWrapper);
        List<MaterialType> mapList = new ArrayList<>();
        for (MaterialType data : dataList) {
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if (pidVal != null && !IMaterialTypeService.NOCHILD.equals(pidVal)) {
                MaterialType rootVal = this.getTreeRoot(pidVal);
                if (rootVal != null && !mapList.contains(rootVal)) {
                    mapList.add(rootVal);
                }
            } else {
                if (!mapList.contains(data)) {
                    mapList.add(data);
                }
            }
        }
        return mapList;
    }

    @Override
    public List<SelectTreeModel> queryListByCode(String parentCode) {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(parentCode)) {
            LambdaQueryWrapper<MaterialType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MaterialType::getPid, parentCode);
            List<MaterialType> list = baseMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0) {
                throw new JeecgBootException("该编码【" + parentCode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new JeecgBootException("该编码【" + parentCode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

    /**
     * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
     *
     * @param pid
     */
    private void updateOldParentNode(String pid) {
        if (!IMaterialTypeService.ROOT_PID_VALUE.equals(pid)) {
            Long count = baseMapper.selectCount(new QueryWrapper<MaterialType>().eq("pid", pid));
            if (count == null || count <= 1) {
                baseMapper.updateTreeNodeStatus(pid, IMaterialTypeService.NOCHILD);
            }
        }
    }

    /**
     * 递归查询节点的根节点
     *
     * @param pidVal
     * @return
     */
    private MaterialType getTreeRoot(String pidVal) {
        MaterialType data = baseMapper.selectById(pidVal);
        if (data != null && !IMaterialTypeService.ROOT_PID_VALUE.equals(data.getPid())) {
            return this.getTreeRoot(data.getPid());
        } else {
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     *
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if (pidVal != null) {
                if (!sb.toString().contains(pidVal)) {
                    if (sb.toString().length() > 0) {
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal, sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     *
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal, StringBuffer sb) {
        List<MaterialType> dataList = baseMapper.selectList(new QueryWrapper<MaterialType>().eq("pid", pidVal));
        if (dataList != null && dataList.size() > 0) {
            for (MaterialType tree : dataList) {
                if (!sb.toString().contains(tree.getId())) {
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(), sb);
            }
        }
        return sb;
    }

}
