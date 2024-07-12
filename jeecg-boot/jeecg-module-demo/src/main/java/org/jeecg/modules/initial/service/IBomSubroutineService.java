package org.jeecg.modules.initial.service;

import org.jeecg.modules.bill.entity.BillingDetail;
import org.jeecg.modules.initial.entity.BomSubroutine;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * @Description: 子件明细
 * @Author: jeecg-boot
 * @Date:   2023-05-01
 * @Version: V1.0
 */
public interface IBomSubroutineService extends IService<BomSubroutine> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<BomSubroutine>
	 */
	public List<BomSubroutine> selectByMainId(String mainId);

	public void selectAllById(List<BillingDetail> billingDetails, BomSubroutine bomSubroutine, Double index, Date demandedDate);
}
