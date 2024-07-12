package org.jeecg.modules.initial.service.impl;

import org.jeecg.modules.initial.entity.Supplier;
import org.jeecg.modules.initial.mapper.SupplierMapper;
import org.jeecg.modules.initial.service.ISupplierService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 供应商补充信息
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {
	
	@Autowired
	private SupplierMapper supplierMapper;
	
	@Override
	public List<Supplier> selectByMainId(String mainId) {
		return supplierMapper.selectByMainId(mainId);
	}
}
