package org.jeecg.modules.bill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.BomRouting;
import org.jeecg.modules.bill.entity.CraftDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.bill.entity.PayDetail;

import java.util.List;

/**
 * @Description: 工艺明细
 * @Author: jeecg-boot
 * @Date:   2023-05-04
 * @Version: V1.0
 */
public interface ICraftDetailService extends IService<CraftDetail> {
	/**
	 * 根据单据保存付款明细
	 */
	public void saveCraftDetails(Bill bill, List<CraftDetail> craftDetailList);
	/**
	 * 根据单据删除付款明细
	 */
	public void delCraftDetails(Bill bill);

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CraftDetail>
	 */
	public List<CraftDetail> selectByMainId(String mainId);
	/**
	 * 参照单据的工艺路线
	 */
	IPage<CraftDetail> queryCraftDetailList(String type, String billCode, String clientId,String productId,
										  String beginTime, String endTime, int pageNo, int pageSize);
}
