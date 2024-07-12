package org.jeecg.modules.initial.service;

import org.jeecg.modules.initial.entity.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 客户补充信息
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
public interface ICustomerService extends IService<Customer> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<Customer>
	 */
	public List<Customer> selectByMainId(String mainId);
}
