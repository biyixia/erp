package org.jeecg.modules.voucher.service.impl;

import org.jeecg.modules.voucher.entity.Voucher;
import org.jeecg.modules.voucher.entity.EntryDetail;
import org.jeecg.modules.voucher.mapper.EntryDetailMapper;
import org.jeecg.modules.voucher.mapper.VoucherMapper;
import org.jeecg.modules.voucher.service.IVoucherService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 填制凭证
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

	@Autowired
	private VoucherMapper voucherMapper;
	@Autowired
	private EntryDetailMapper entryDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(Voucher voucher, List<EntryDetail> entryDetailList) {
		voucherMapper.insert(voucher);
		if(entryDetailList!=null && entryDetailList.size()>0) {
			for(EntryDetail entity:entryDetailList) {
				//外键设置
				entity.setVoucherId(voucher.getId());
				entryDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(Voucher voucher,List<EntryDetail> entryDetailList) {
		voucherMapper.updateById(voucher);
		
		//1.先删除子表数据
		entryDetailMapper.deleteByMainId(voucher.getId());
		
		//2.子表数据重新插入
		if(entryDetailList!=null && entryDetailList.size()>0) {
			for(EntryDetail entity:entryDetailList) {
				//外键设置
				entity.setVoucherId(voucher.getId());
				entryDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		entryDetailMapper.deleteByMainId(id);
		voucherMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			entryDetailMapper.deleteByMainId(id.toString());
			voucherMapper.deleteById(id);
		}
	}
	
}
