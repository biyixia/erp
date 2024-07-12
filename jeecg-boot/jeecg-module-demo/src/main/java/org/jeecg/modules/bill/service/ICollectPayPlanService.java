package org.jeecg.modules.bill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.BillingDetail;
import org.jeecg.modules.bill.entity.CollectPayPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 款计划
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
public interface ICollectPayPlanService extends IService<CollectPayPlan> {
	/**
	 * 根据单据保存付款计划
	 */
	public void saveCollectPayPlans(Bill bill, List<CollectPayPlan> collectPayPlanList);
	/**
	 * 根据单据删除付款计划
	 */
	public void delCollectPayPlans(Bill bill);
	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CollectPayPlan>
	 */
	public List<CollectPayPlan> selectByMainId(String mainId);

	/**
	 * 参照采购订单数据查询
	 */
	IPage<CollectPayPlan> queryPoPayPageList(boolean total,String type,String billCode, String clientId, String materialParam, String beginTime, String endTime, int pageNo, int pageSize);
}
