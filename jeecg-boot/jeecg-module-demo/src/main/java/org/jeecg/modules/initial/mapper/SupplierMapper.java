package org.jeecg.modules.initial.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.initial.entity.Supplier;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 供应商补充信息
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<Supplier>
   */
	public List<Supplier> selectByMainId(@Param("mainId") String mainId);
}
