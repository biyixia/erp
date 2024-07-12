package org.jeecg.modules.bill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.CollectPayPlan;
import org.jeecg.modules.bill.entity.PayDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 付款明细
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
public interface IPayDetailService extends IService<PayDetail> {
	/**
	 * 根据单据保存付款明细
	 */
	public void savePayDetails(Bill bill, List<PayDetail> payDetailList);
	/**
	 * 根据单据删除付款明细
	 */
	public void delPayDetails(Bill bill);
	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<PayDetail>
	 */
	public List<PayDetail> selectByMainId(String mainId);
	/**
	 * 参照单据付款明细查询
	 */
	IPage<PayDetail> queryPayDetailList(boolean total,String type,String billCode, String clientId, String materialParam, String beginTime, String endTime, int pageNo, int pageSize);
}
