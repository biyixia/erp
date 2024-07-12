package org.jeecg.modules.bill.vo;

import java.util.List;

import org.jeecg.modules.bill.entity.*;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 单据管理
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@Data
@ApiModel(value="byx_billPage对象", description="单据管理")
public class BillPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**单据编码*/
	@Excel(name = "单据编码", width = 15)
	@ApiModelProperty(value = "单据编码")
    private java.lang.String code;
	/**单据日期*/
	@Excel(name = "单据日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "单据日期")
    private java.util.Date billDate;
	/**人员名称*/
	@Excel(name = "人员名称", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@ApiModelProperty(value = "人员名称")
    private java.lang.String personName;
	/**部门名称*/
	@Excel(name = "部门名称", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
	@ApiModelProperty(value = "部门名称")
    private java.lang.String departmentName;
	/**客商名称*/
	@Excel(name = "客商名称", width = 15)
	@ApiModelProperty(value = "客商名称")
    private java.lang.String clientId;
	/**预计发货日期*/
	@Excel(name = "预计发货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "预计发货日期")
    private java.util.Date estimatedDeliveryDate;
	/**付款政策*/
	@Excel(name = "付款政策", width = 15, dicCode = "payment_style")
    @Dict(dicCode = "payment_style")
	@ApiModelProperty(value = "付款政策")
    private java.lang.String paymentStyle;
	/**发票类别*/
	@Excel(name = "发票类别", width = 15, dicCode = "invoice_type")
    @Dict(dicCode = "invoice_type")
	@ApiModelProperty(value = "发票类别")
    private java.lang.String invoiceType;
	/**发票号码*/
	@Excel(name = "发票号码", width = 15)
	@ApiModelProperty(value = "发票号码")
    private java.lang.String invoiceNumber;
	/**调入仓库*/
	@Excel(name = "调入仓库", width = 15)
	@ApiModelProperty(value = "调入仓库")
    private java.lang.String inWarehouseId;
	/**调出仓库*/
	@Excel(name = "调出仓库", width = 15)
	@ApiModelProperty(value = "调出仓库")
    private java.lang.String outWarehouseId;
	/**需求日期*/
	@Excel(name = "需求日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "需求日期")
    private java.util.Date demandedDate;
	/**产品长编码*/
	@Excel(name = "产品id", width = 15)
	@ApiModelProperty(value = "产品id")
    private java.lang.String productId;
	/**bomId*/
	@Excel(name = "bomId", width = 15)
	@ApiModelProperty(value = "bomId")
	private java.lang.String bomId;
	/**生产数量*/
	@Excel(name = "生产数量", width = 15)
	@ApiModelProperty(value = "生产数量")
    private java.lang.Double produceNumber;
	/**支出账户*/
	@Excel(name = "支出账户", width = 15)
	@ApiModelProperty(value = "支出账户")
    private java.lang.String accountPayment;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**单据模式*/
	@Excel(name = "单据模式", width = 15, dicCode = "bill_mode")
    @Dict(dicCode = "bill_mode")
	@ApiModelProperty(value = "单据模式")
    private java.lang.String billMode;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	
	@ExcelCollection(name="标的明细")
	@ApiModelProperty(value = "标的明细")
	private List<BillingDetail> billingDetailList;
	@ExcelCollection(name="款计划")
	@ApiModelProperty(value = "款计划")
	private List<CollectPayPlan> collectPayPlanList;
	@ExcelCollection(name="付款明细")
	@ApiModelProperty(value = "付款明细")
	private List<PayDetail> payDetailList;
	@ExcelCollection(name="工艺线路")
	@ApiModelProperty(value = "工艺线路")
	private List<BomRouting> bomRoutingList;
	@ExcelCollection(name="工艺明细")
	@ApiModelProperty(value = "工艺明细")
	private List<CraftDetail> craftDetailList;
	
}
