package org.jeecg.modules.voucher.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @Description: 分录明细
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@ApiModel(value="byx_entry_detail对象", description="分录明细")
@Data
@TableName("byx_entry_detail")
public class EntryDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**摘要*/
	@Excel(name = "摘要", width = 15)
    @ApiModelProperty(value = "摘要")
    private String digest;
	/**科目编码*/
	@Excel(name = "科目编码", width = 15)
    @ApiModelProperty(value = "科目编码")
    private String subCode;
	/**科目名称*/
	@Excel(name = "科目名称", width = 15)
    @ApiModelProperty(value = "科目名称")
    private String subName;
	/**借方金额*/
	@Excel(name = "借方金额", width = 15, dicCode = "debit_credit_money")
	@Dict(dicCode = "debit_credit_money")
    @ApiModelProperty(value = "借方金额")
    private String debitMoney;
	/**贷方金额*/
	@Excel(name = "贷方金额", width = 15, dicCode = "debit_credit_money")
	@Dict(dicCode = "debit_credit_money")
    @ApiModelProperty(value = "贷方金额")
    private String creditMoney;
	/**账户*/
	@Excel(name = "账户", width = 15)
    @ApiModelProperty(value = "账户")
    private String isAccount;
	/**仓库*/
	@Excel(name = "仓库", width = 15)
    @ApiModelProperty(value = "仓库")
    private String isWarehouse;
	/**费用*/
	@Excel(name = "费用", width = 15)
    @ApiModelProperty(value = "费用")
    private String isCost;
	/**明细*/
	@Excel(name = "明细", width = 15)
    @ApiModelProperty(value = "明细")
    private String isDetail;
	/**现金流量名称*/
	@Excel(name = "现金流量名称", width = 15)
    @ApiModelProperty(value = "现金流量名称")
    private String cashFlowName;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**凭证模板id*/
    @ApiModelProperty(value = "凭证模板id")
    private String voucherId;
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private java.lang.String delFlag;
}
