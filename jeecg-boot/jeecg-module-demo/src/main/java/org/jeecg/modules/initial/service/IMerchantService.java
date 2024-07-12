package org.jeecg.modules.initial.service;

import org.jeecg.modules.initial.entity.Customer;
import org.jeecg.modules.initial.entity.Supplier;
import org.jeecg.modules.initial.entity.Merchant;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 客商管理
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
public interface IMerchantService extends IService<Merchant> {

	/**
	 * 添加一对多
	 *
	 * @param merchant
	 * @param customerList
	 * @param supplierList
	 */
	public void saveMain(Merchant merchant,List<Customer> customerList,List<Supplier> supplierList) ;
	
	/**
	 * 修改一对多
	 *
	 * @param merchant
	 * @param customerList
	 * @param supplierList
	 */
	public void updateMain(Merchant merchant,List<Customer> customerList,List<Supplier> supplierList);
	
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
