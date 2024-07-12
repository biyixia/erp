package org.jeecg.modules.bill.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 款计划
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@ApiModel(value="byx_collect_pay_plan对象", description="款计划")
@Data
@TableName("byx_collect_pay_plan")
public class CollectPayPlan implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**付款类型*/
	@Excel(name = "付款类型", width = 15, dicCode = "payment_type")
	@Dict(dicCode = "payment_type")
    @ApiModelProperty(value = "付款类型")
    private java.lang.String payType;
	/**付款比例*/
	@Excel(name = "付款比例", width = 15)
    @ApiModelProperty(value = "付款比例")
    private java.lang.Double payRatio;
	/**预计付款日期*/
	@Excel(name = "预计付款日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "预计付款日期")
    private java.util.Date expectedPayDate;
	/**付款金额*/
	@Excel(name = "付款金额", width = 15)
    @ApiModelProperty(value = "付款金额")
    private java.lang.Double payAmount;
    /**累计申请金额*/
    @Excel(name = "累计申请金额", width = 15)
    @ApiModelProperty(value = "累计申请金额")
    private java.lang.Double sumReqAmount;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**单据id*/
    @ApiModelProperty(value = "单据id")
    private java.lang.String billId;
    @TableField(exist = false)
    private Bill bill;
}
