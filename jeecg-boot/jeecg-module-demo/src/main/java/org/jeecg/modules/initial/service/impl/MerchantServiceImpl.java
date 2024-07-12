package org.jeecg.modules.initial.service.impl;

import org.jeecg.modules.initial.entity.Merchant;
import org.jeecg.modules.initial.entity.Customer;
import org.jeecg.modules.initial.entity.Supplier;
import org.jeecg.modules.initial.mapper.CustomerMapper;
import org.jeecg.modules.initial.mapper.SupplierMapper;
import org.jeecg.modules.initial.mapper.MerchantMapper;
import org.jeecg.modules.initial.service.IMerchantService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 客商管理
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements IMerchantService {

	@Autowired
	private MerchantMapper merchantMapper;
	@Autowired
	private CustomerMapper customerMapper;
	@Autowired
	private SupplierMapper supplierMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(Merchant merchant, List<Customer> customerList,List<Supplier> supplierList) {
		merchantMapper.insert(merchant);
		if(customerList!=null && customerList.size()>0) {
			for(Customer entity:customerList) {
				//外键设置
				entity.setMerchantId(merchant.getId());
				customerMapper.insert(entity);
			}
		}
		if(supplierList!=null && supplierList.size()>0) {
			for(Supplier entity:supplierList) {
				//外键设置
				entity.setMerchantId(merchant.getId());
				supplierMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(Merchant merchant,List<Customer> customerList,List<Supplier> supplierList) {
		merchantMapper.updateById(merchant);
		
		//1.先删除子表数据
		customerMapper.deleteByMainId(merchant.getId());
		supplierMapper.deleteByMainId(merchant.getId());
		
		//2.子表数据重新插入
		if(customerList!=null && customerList.size()>0) {
			for(Customer entity:customerList) {
				//外键设置
				entity.setMerchantId(merchant.getId());
				customerMapper.insert(entity);
			}
		}
		if(supplierList!=null && supplierList.size()>0) {
			for(Supplier entity:supplierList) {
				//外键设置
				entity.setMerchantId(merchant.getId());
				supplierMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		customerMapper.deleteByMainId(id);
		supplierMapper.deleteByMainId(id);
		merchantMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			customerMapper.deleteByMainId(id.toString());
			supplierMapper.deleteByMainId(id.toString());
			merchantMapper.deleteById(id);
		}
	}
	
}
