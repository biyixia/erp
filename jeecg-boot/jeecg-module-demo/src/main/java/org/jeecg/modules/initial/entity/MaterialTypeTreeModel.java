package org.jeecg.modules.initial.entity;


import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
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
public class MaterialTypeTreeModel implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 对应SysDepart中的id字段,前端数据树中的key*/
    private String key;

    /** 对应SysDepart中的id字段,前端数据树中的value*/
    private String value;

    /** 对应depart_name字段,前端数据树中的title*/
    private String title;

    private boolean isLeaf;
    // 以下所有字段均与SysDepart相同


    /**主键*/
    private java.lang.String id;
    /**分类编码*/
    private java.lang.String code;
    /**分类名称*/
    private java.lang.String name;
    /**上级分类*/
    private java.lang.String pid;
    /**显示顺序*/
    private java.lang.String sorter;
    /**备注*/
    private java.lang.String remark;
    /**创建人*/
    private java.lang.String createBy;
    /**创建日期*/
    private java.util.Date createTime;
    /**更新人*/
    private java.lang.String updateBy;
    /**更新日期*/
    private java.util.Date updateTime;
    /**所属部门*/
    private java.lang.String sysOrgCode;
    /**是否有子节点*/
    private java.lang.String hasChild;
    private List<MaterialTypeTreeModel> children = new ArrayList<>();


    /**
     * 将MaterialType对象转换成MaterialTypeTreeModel对象
     * @param materialType
     */
	public MaterialTypeTreeModel(MaterialType materialType) {
		this.key = materialType.getId();
        this.value = materialType.getId();
        this.title = materialType.getName();
        this.id = materialType.getId();
        this.pid = materialType.getPid();
        this.code = materialType.getCode();
        this.name = materialType.getName();
        this.sorter = materialType.getSorter();
        this.remark = materialType.getRemark();
        this.createBy = materialType.getCreateBy();
        this.createTime = materialType.getCreateTime();
        this.updateBy = materialType.getUpdateBy();
        this.updateTime = materialType.getUpdateTime();
    }

    public void setChildren(List<MaterialTypeTreeModel> children) {
        if (children==null){
            this.isLeaf=true;
        }
        this.children = children;
    }


    public void setIsLeaf(boolean b) {
        this.isLeaf = b;
    }

    public boolean getIsLeaf() {
        return this.isLeaf;
    }
}
