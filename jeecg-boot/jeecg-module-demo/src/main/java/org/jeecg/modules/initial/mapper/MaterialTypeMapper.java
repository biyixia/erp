package org.jeecg.modules.initial.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.initial.entity.MaterialType;

import java.util.List;
import java.util.Map;

/**
 * @Description: 物料类型
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@Mapper
public interface MaterialTypeMapper extends BaseMapper<MaterialType> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

	/**
	 * 【vue3专用】根据父级ID查询树节点数据
	 *
	 * @param pid
	 * @param query
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(@Param("pid") String pid, @Param("query") Map<String, String> query);

	/**
	 * 根据id下级物料类型数量
	 * @param parentId
	 * @return
	 */
	@Select("SELECT count(*) FROM byx_material_type where del_flag ='0' AND pid = #{parentId,jdbcType=VARCHAR}")
	Integer queryCountByPid(@org.springframework.data.repository.query.Param("parentId")String parentId);
}
