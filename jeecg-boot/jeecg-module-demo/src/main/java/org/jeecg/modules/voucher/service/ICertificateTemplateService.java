package org.jeecg.modules.voucher.service;

import org.jeecg.modules.voucher.entity.EntryDetail;
import org.jeecg.modules.voucher.entity.CertificateTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 凭证模板
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
public interface ICertificateTemplateService extends IService<CertificateTemplate> {

	/**
	 * 添加一对多
	 *
	 * @param certificateTemplate
	 * @param entryDetailList
	 */
	public void saveMain(CertificateTemplate certificateTemplate,List<EntryDetail> entryDetailList) ;
	
	/**
	 * 修改一对多
	 *
	 * @param certificateTemplate
	 * @param entryDetailList
	 */
	public void updateMain(CertificateTemplate certificateTemplate,List<EntryDetail> entryDetailList);
	
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
