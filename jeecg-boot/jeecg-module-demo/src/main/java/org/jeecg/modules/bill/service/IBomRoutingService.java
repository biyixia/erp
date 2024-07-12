package org.jeecg.modules.bill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.BillingDetail;
import org.jeecg.modules.bill.entity.BomRouting;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 工艺线路
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
public interface IBomRoutingService extends IService<BomRouting> {
	/**
	 * 参照单据的工艺路线
	 */
	IPage<BomRouting> queryBomRoutingList(boolean total,String type, String consign ,String billCode, String productId,
										 String beginTime, String endTime, int pageNo, int pageSize);
	/**
	 * 根据单据保存工艺线路
	 */
	public void saveBomRoutings(Bill bill, List<BomRouting> bomRoutingList);
	/**
	 * 根据单据删除工艺线路
	 */
	public void delBomRoutings(Bill bill);
	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<BomRouting>
	 */
	public List<BomRouting> selectByMainId(String mainId);
	/**
	 * 通过bom id查询子表数据
	 *
	 * @param bomId
	 * @return List<BomRouting>
	 */
	public List<BomRouting> selectByBomId(String bomId);
}
