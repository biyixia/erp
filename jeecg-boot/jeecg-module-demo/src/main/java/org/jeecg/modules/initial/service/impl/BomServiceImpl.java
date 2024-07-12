package org.jeecg.modules.initial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.bill.entity.BomRouting;
import org.jeecg.modules.bill.mapper.BomRoutingMapper;
import org.jeecg.modules.initial.entity.Bom;
import org.jeecg.modules.initial.entity.BomSubroutine;
import org.jeecg.modules.initial.mapper.BomSubroutineMapper;
import org.jeecg.modules.initial.mapper.BomMapper;
import org.jeecg.modules.initial.service.IBomService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 物料清单
 * @Author: jeecg-boot
 * @Date:   2023-05-01
 * @Version: V1.0
 */
@Service
public class BomServiceImpl extends ServiceImpl<BomMapper, Bom> implements IBomService {

	@Autowired
	private BomMapper bomMapper;
	@Autowired
	private BomSubroutineMapper bomSubroutineMapper;
	@Autowired
	private BomRoutingMapper bomRoutingMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveMain(Bom bom, List<BomSubroutine> bomSubroutineList, List<BomRouting> bomRoutingList) {
		if ((bom.getIsDefault().equals("Y") && bomMapper.getDefaultByProd(bom.getProdId()) < 1) || bom.getIsDefault().equals("N")) {
			bomMapper.insert(bom);
			if (bomSubroutineList != null && bomSubroutineList.size() > 0) {
				for (BomSubroutine entity : bomSubroutineList) {
					//外键设置
					entity.setBomId(bom.getId());
					bomSubroutineMapper.insert(entity);
				}
			}
			if (bomRoutingList != null && bomRoutingList.size() > 0) {
				for (BomRouting entity : bomRoutingList) {
					//外键设置
					entity.setBomId(bom.getId());
					bomRoutingMapper.insert(entity);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateMain(Bom bom,List<BomSubroutine> bomSubroutineList, List<BomRouting> bomRoutingList) {
		if (bom.getIsDefault().equals(bomMapper.selectById(bom.getId()).getIsDefault()) || (bom.getIsDefault().equals("Y") && bomMapper.getDefaultByProd(bom.getProdId()) == 0)
				|| bom.getIsDefault().equals("N")
		) {
			bomMapper.updateById(bom);
			//1.先删除子表数据
			bomSubroutineMapper.deleteByMainId(bom.getId());
			bomRoutingMapper.delete(new QueryWrapper<BomRouting>().eq("bom_id", bom.getId()));
			//2.子表数据重新插入
			if (bomSubroutineList != null && bomSubroutineList.size() > 0) {
				for (BomSubroutine entity : bomSubroutineList) {
					//外键设置
					entity.setBomId(bom.getId());
					bomSubroutineMapper.insert(entity);
				}
			}
			if (bomRoutingList != null && bomRoutingList.size() > 0) {
				for (BomRouting entity : bomRoutingList) {
					//外键设置
					entity.setBomId(bom.getId());
					bomRoutingMapper.insert(entity);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		bomSubroutineMapper.deleteByMainId(id);
		bomRoutingMapper.delete(new QueryWrapper<BomRouting>().eq("bom_id",id));
		bomMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			bomSubroutineMapper.deleteByMainId(id.toString());
			bomRoutingMapper.delete(new QueryWrapper<BomRouting>().eq("bom_id",id));
			bomMapper.deleteById(id);
		}
	}


}
