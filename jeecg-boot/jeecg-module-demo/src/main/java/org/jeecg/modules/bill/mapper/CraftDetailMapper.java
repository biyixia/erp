package org.jeecg.modules.bill.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.bill.entity.CraftDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 工艺明细
 * @Author: jeecg-boot
 * @Date:   2023-05-04
 * @Version: V1.0
 */
@Mapper
public interface CraftDetailMapper extends BaseMapper<CraftDetail> {

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
   * @return List<CraftDetail>
   */
	public List<CraftDetail> selectByMainId(@Param("mainId") String mainId);
}
