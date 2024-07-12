package org.jeecg.modules.voucher.service;

import org.jeecg.modules.voucher.entity.EntryDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 分录明细
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
public interface IEntryDetailService extends IService<EntryDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<EntryDetail>
	 */
	public List<EntryDetail> selectByMainId(String mainId);
}
