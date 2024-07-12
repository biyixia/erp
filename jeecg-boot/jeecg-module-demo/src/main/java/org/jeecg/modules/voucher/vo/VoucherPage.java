package org.jeecg.modules.voucher.vo;

import java.util.List;
import org.jeecg.modules.voucher.entity.Voucher;
import org.jeecg.modules.voucher.entity.EntryDetail;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 填制凭证
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@Data
@ApiModel(value="byx_voucherPage对象", description="填制凭证")
public class VoucherPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
    private String id;
	/**凭证类型*/
	@Excel(name = "凭证类型", width = 15)
	@ApiModelProperty(value = "凭证类型")
    private String voucherType;
	/**凭证日期*/
	@Excel(name = "凭证日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "凭证日期")
    private Date voucherDate;
	/**凭证号*/
	@Excel(name = "凭证号", width = 15)
	@ApiModelProperty(value = "凭证号")
    private String voucherNumber;
	/**附件数量*/
	@Excel(name = "附件数量", width = 15)
	@ApiModelProperty(value = "附件数量")
    private Double attachNumber;
	
	@ExcelCollection(name="分录明细")
	@ApiModelProperty(value = "分录明细")
	private List<EntryDetail> entryDetailList;
	
}
