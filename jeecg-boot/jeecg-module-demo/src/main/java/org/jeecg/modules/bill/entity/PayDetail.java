package org.jeecg.modules.bill.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.modules.initial.entity.Merchant;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 付款明细
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@ApiModel(value="byx_pay_detail对象", description="付款明细")
@Data
@TableName("byx_pay_detail")
public class PayDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**客商名称*/
	@Excel(name = "客商名称", width = 15)
    @ApiModelProperty(value = "客商名称")
    private java.lang.String clientId;
	/**付款类型*/
	@Excel(name = "付款类型", width = 15, dicCode = "payment_type")
	@Dict(dicCode = "payment_type")
    @ApiModelProperty(value = "付款类型")
    private java.lang.String payType;
	/**申请金额*/
	@Excel(name = "申请金额", width = 15)
    @ApiModelProperty(value = "申请金额")
    private java.lang.Double applyAmount;
	/**预计付款日期*/
	@Excel(name = "预计付款日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "预计付款日期")
    private java.util.Date expectedPayDate;
	/**单据应付金额*/
	@Excel(name = "单据应付金额", width = 15)
    @ApiModelProperty(value = "单据应付金额")
    private java.lang.Double payAmount;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**来源*/
	@Excel(name = "来源", width = 15)
    @ApiModelProperty(value = "来源")
    private java.lang.String source;
	/**来源单号*/
	@Excel(name = "来源单号", width = 15)
    @ApiModelProperty(value = "来源单号")
    private java.lang.String sourceNumber;
	/**单据id*/
    @ApiModelProperty(value = "单据id")
    private java.lang.String byxBill;
    /**v*/
    @Excel(name = "累计付款金额", width = 15)
    @ApiModelProperty(value = "累计付款金额")
    private java.lang.Double sumApplyAmount;
    //引用id，用于参照其他表
    private String referId;
    /**供应商详情*/
    @TableField(exist = false)
    private Merchant merchant;
    @TableField(exist = false)
    private Bill bill;
}
