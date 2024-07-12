package org.jeecg.modules.voucher.service.impl;

import org.jeecg.modules.voucher.entity.CertificateTemplate;
import org.jeecg.modules.voucher.entity.EntryDetail;
import org.jeecg.modules.voucher.mapper.EntryDetailMapper;
import org.jeecg.modules.voucher.mapper.CertificateTemplateMapper;
import org.jeecg.modules.voucher.service.ICertificateTemplateService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 凭证模板
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@Service
public class CertificateTemplateServiceImpl extends ServiceImpl<CertificateTemplateMapper, CertificateTemplate> implements ICertificateTemplateService {

	@Autowired
	private CertificateTemplateMapper certificateTemplateMapper;
	@Autowired
	private EntryDetailMapper entryDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(CertificateTemplate certificateTemplate, List<EntryDetail> entryDetailList) {
		certificateTemplateMapper.insert(certificateTemplate);
		if(entryDetailList!=null && entryDetailList.size()>0) {
			for(EntryDetail entity:entryDetailList) {
				//外键设置
				entity.setVoucherId(certificateTemplate.getId());
				entryDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(CertificateTemplate certificateTemplate,List<EntryDetail> entryDetailList) {
		certificateTemplateMapper.updateById(certificateTemplate);
		
		//1.先删除子表数据
		entryDetailMapper.deleteByMainId(certificateTemplate.getId());
		
		//2.子表数据重新插入
		if(entryDetailList!=null && entryDetailList.size()>0) {
			for(EntryDetail entity:entryDetailList) {
				//外键设置
				entity.setVoucherId(certificateTemplate.getId());
				entryDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		entryDetailMapper.deleteByMainId(id);
		certificateTemplateMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			entryDetailMapper.deleteByMainId(id.toString());
			certificateTemplateMapper.deleteById(id);
		}
	}
	
}
