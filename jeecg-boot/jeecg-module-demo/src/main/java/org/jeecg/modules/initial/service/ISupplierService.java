package org.jeecg.modules.initial.service;

import org.jeecg.modules.initial.entity.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 供应商补充信息
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
public interface ISupplierService extends IService<Supplier> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<Supplier>
	 */
	public List<Supplier> selectByMainId(String mainId);
}
