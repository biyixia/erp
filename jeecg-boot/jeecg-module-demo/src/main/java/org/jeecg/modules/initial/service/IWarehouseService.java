package org.jeecg.modules.initial.service;

import org.jeecg.modules.initial.entity.Warehouse;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 仓库档案
 * @Author: jeecg-boot
 * @Date:   2023-04-29
 * @Version: V1.0
 */
public interface IWarehouseService extends IService<Warehouse> {

	/**
	 * 添加一对多
	 *
	 * @param warehouse
	 */
	public void saveMain(Warehouse warehouse) ;
	
	/**
	 * 修改一对多
	 *
	 * @param warehouse
	 */
	public void updateMain(Warehouse warehouse);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
