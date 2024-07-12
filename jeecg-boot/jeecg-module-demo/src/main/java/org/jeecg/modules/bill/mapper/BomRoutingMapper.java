package org.jeecg.modules.bill.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.bill.entity.BomRouting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 工艺线路
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@Mapper
public interface BomRoutingMapper extends BaseMapper<BomRouting> {

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
   * @return List<BomRouting>
   */
	public List<BomRouting> selectByMainId(@Param("mainId") String mainId);
}
