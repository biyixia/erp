package org.jeecg.modules.initial.service;

import org.jeecg.modules.bill.entity.BomRouting;
import org.jeecg.modules.initial.entity.BomSubroutine;
import org.jeecg.modules.initial.entity.Bom;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 物料清单
 * @Author: jeecg-boot
 * @Date:   2023-05-01
 * @Version: V1.0
 */
public interface IBomService extends IService<Bom> {

	/**
	 * 添加一对多
	 *
	 * @param bom
	 * @param bomSubroutineList
	 */
	public boolean saveMain(Bom bom, List<BomSubroutine> bomSubroutineList, List<BomRouting> bomRoutingList) ;
	
	/**
	 * 修改一对多
	 *
	 * @param bom
	 * @param bomSubroutineList
	 */
	public boolean updateMain(Bom bom,List<BomSubroutine> bomSubroutineList, List<BomRouting> bomRoutingList);
	
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
