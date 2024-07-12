package org.jeecg.modules.initial.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.initial.entity.BomSubroutine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 子件明细
 * @Author: jeecg-boot
 * @Date:   2023-05-01
 * @Version: V1.0
 */
@Mapper
public interface BomSubroutineMapper extends BaseMapper<BomSubroutine> {

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
   * @return List<BomSubroutine>
   */
	public List<BomSubroutine> selectByMainId(@Param("mainId") String mainId);
}
