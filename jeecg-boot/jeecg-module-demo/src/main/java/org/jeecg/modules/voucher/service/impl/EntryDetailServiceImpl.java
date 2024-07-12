package org.jeecg.modules.voucher.service.impl;

import org.jeecg.modules.voucher.entity.EntryDetail;
import org.jeecg.modules.voucher.mapper.EntryDetailMapper;
import org.jeecg.modules.voucher.service.IEntryDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 分录明细
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@Service
public class EntryDetailServiceImpl extends ServiceImpl<EntryDetailMapper, EntryDetail> implements IEntryDetailService {
	
	@Autowired
	private EntryDetailMapper entryDetailMapper;
	
	@Override
	public List<EntryDetail> selectByMainId(String mainId) {
		return entryDetailMapper.selectByMainId(mainId);
	}
}
