package org.jeecg.modules.initial.entity;

import lombok.Data;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.model.DepartIdModel;
import org.jeecg.modules.system.model.SysDepartTreeModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 部门表 存储树结构数据的实体类
 * <p>
 * 
 * @Author Steve
 * @Since 2019-01-22 
 */
@Data
public class MaterialTypeIdModel implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 对应SysDepart中的id字段,前端数据树中的key*/
    private String key;

    /** 对应SysDepart中的id字段,前端数据树中的value*/
    private String value;

    /** 对应depart_name字段,前端数据树中的title*/
    private String title;

    List<MaterialTypeIdModel> children = new ArrayList<>();




    /**
     * 将MaterialTypeTreeModel的部分数据放在该对象当中
     * @param treeModel
     * @return
     */
    public MaterialTypeIdModel convert(MaterialTypeTreeModel treeModel) {
        this.key = treeModel.getId();
        this.value = treeModel.getId();
        this.title = treeModel.getName();
        return this;
    }

    /**
     * 该方法为物料类型的实现类所使用
     * @param materialType
     * @return
     */
    public MaterialTypeIdModel convertByUserDepart(MaterialType materialType) {
        this.key = materialType.getId();
        this.value = materialType.getId();
        this.title = materialType.getName();
        return this;
    }


}
