package org.jeecg.modules.initial.service.impl;

import org.jeecg.modules.initial.entity.Warehouse;
import org.jeecg.modules.initial.mapper.WarehouseMapper;
import org.jeecg.modules.initial.service.IWarehouseService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 仓库档案
 * @Author: jeecg-boot
 * @Date:   2023-04-29
 * @Version: V1.0
 */
@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements IWarehouseService {

	@Autowired
	private WarehouseMapper warehouseMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(Warehouse warehouse) {
		warehouseMapper.insert(warehouse);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(Warehouse warehouse) {
		warehouseMapper.updateById(warehouse);
		
		//1.先删除子表数据
		
		//2.子表数据重新插入
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		warehouseMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			warehouseMapper.deleteById(id);
		}
	}
	
}
