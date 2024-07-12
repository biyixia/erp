package org.jeecg.modules.initial.service.impl;

import org.jeecg.modules.initial.entity.Customer;
import org.jeecg.modules.initial.mapper.CustomerMapper;
import org.jeecg.modules.initial.service.ICustomerService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 客户补充信息
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {
	
	@Autowired
	private CustomerMapper customerMapper;
	
	@Override
	public List<Customer> selectByMainId(String mainId) {
		return customerMapper.selectByMainId(mainId);
	}
}
