package org.jeecg.modules.initial.service;

import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.initial.entity.MaterialType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.initial.entity.MaterialTypeTreeModel;
import org.jeecg.modules.system.model.SysDepartTreeModel;

import java.util.List;

/**
 * @Description: 物料类型
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
public interface IMaterialTypeService extends IService<MaterialType> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";
	
	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";
	
	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 获取物料类型下的所有物料类型
	 * @param parentId 父id
	 * @param ids 多个物料类型id
	 * @param primaryKey 主键字段（id或者orgCode）
	 * @return
	 */
	List<MaterialTypeTreeModel> queryTreeListByPid(String parentId,String ids, String primaryKey);


	/**
	 * 查询所有物料类型信息,并分节点进行显示
	 * @param ids 多个物料id
	 * @return
	 */
	List<MaterialTypeTreeModel> queryTreeList(String ids);

	/**
	 * 查询所有物料类型信息,并分节点进行显示
	 * @return
	 */
	List<MaterialTypeTreeModel> queryTreeList();

	/**
	 * 新增节点
	 *
	 * @param materialType
	 */
	void addMaterialType(MaterialType materialType);
	
	/**
   * 修改节点
   *
   * @param materialType
   * @throws JeecgBootException
   */
	void updateMaterialType(MaterialType materialType) throws JeecgBootException;
	
	/**
	 * 删除节点
	 *
	 * @param id
   * @throws JeecgBootException
	 */
	void deleteMaterialType(String id) throws JeecgBootException;

	  /**
	   * 查询所有数据，无分页
	   *
	   * @param queryWrapper
	   * @return List<MaterialType>
	   */
    List<MaterialType> queryTreeListNoPage(QueryWrapper<MaterialType> queryWrapper);

	/**
	 * 查询分类下所有孩子的id
	 *
	 * @param materialType
	 */
	void getAllChildIds(MaterialType materialType,List<String> ids);

	/**
	 * 【vue3专用】根据父级编码加载分类字典的数据
	 *
	 * @param parentCode
	 * @return
	 */
	List<SelectTreeModel> queryListByCode(String parentCode);

	/**
	 * 【vue3专用】根据pid查询子节点集合
	 *
	 * @param pid
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(String pid);

}
