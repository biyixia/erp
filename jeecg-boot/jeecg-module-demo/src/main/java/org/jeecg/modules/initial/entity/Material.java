package org.jeecg.modules.initial.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 物料管理
 * @Author: jeecg-boot
 * @Date:   2023-04-07
 * @Version: V1.0
 */
@Data
@TableName("byx_material")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="byx_material对象", description="物料管理")
public class Material implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**物料编码*/
	@Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private java.lang.String code;
	/**物料名称*/
	@Excel(name = "物料名称", width = 15)
    @ApiModelProperty(value = "物料名称")
    private java.lang.String materialName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String size;
	/**所属分类名称*/
	@Excel(name = "所属分类名称", width = 15, dictTable = "byx_material_type", dicText = "name", dicCode = "id")
	@Dict(dictTable = "byx_material_type", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "所属分类名称")
    private java.lang.String owningCode;
	/**计量单位*/
	@Excel(name = "计量单位", width = 15, dicCode = "unit")
	@Dict(dicCode = "unit")
    @ApiModelProperty(value = "计量单位")
    private java.lang.String unit;
	/**含税单价*/
	@Excel(name = "含税单价", width = 15)
    @ApiModelProperty(value = "含税单价")
    private java.lang.Double priceIncludedTax;
	/**件装量*/
	@Excel(name = "件装量", width = 15)
    @ApiModelProperty(value = "件装量")
    private java.lang.Double unitLoad;
	/**税率*/
	@Excel(name = "税率", width = 15)
    @ApiModelProperty(value = "税率")
    private java.lang.Double rate;
    /**无税单价*/
    @Excel(name = "无税单价", width = 15)
    @ApiModelProperty(value = "无税单价")
    private java.lang.Double price;
	/**采购必有合同*/
	@Excel(name = "采购必有合同", width = 15)
    @ApiModelProperty(value = "采购必有合同")
    private java.lang.String isInContract;
	/**销售必有合同*/
	@Excel(name = "销售必有合同", width = 15)
    @ApiModelProperty(value = "销售必有合同")
    private java.lang.String isOutContract;
	/**领用必有订单*/
	@Excel(name = "领用必有订单", width = 15)
    @ApiModelProperty(value = "领用必有订单")
    private java.lang.String isReceiveOrder;
	/**允许超采购合同入库*/
	@Excel(name = "允许超采购合同入库", width = 15)
    @ApiModelProperty(value = "允许超采购合同入库")
    private java.lang.String isBuyorderIn;
	/**允许超采购合同出库*/
	@Excel(name = "允许超采购合同出库", width = 15)
    @ApiModelProperty(value = "允许超采购合同出库")
    private java.lang.String isBuyorderOut;
	/**允许超生产订单领用*/
	@Excel(name = "允许超生产订单领用", width = 15)
    @ApiModelProperty(value = "允许超生产订单领用")
    private java.lang.String isBuyorderReceive;
	/**齐套入库*/
	@Excel(name = "齐套入库", width = 15)
    @ApiModelProperty(value = "齐套入库")
    private java.lang.String completeSetIn;
	/**允许超合同下达生产*/
	@Excel(name = "允许超合同下达生产", width = 15)
    @ApiModelProperty(value = "允许超合同下达生产")
    private java.lang.String isContractProduct;
	/**允许超生产订单入库*/
	@Excel(name = "允许超生产订单入库", width = 15)
    @ApiModelProperty(value = "允许超生产订单入库")
    private java.lang.String isProductIn;
	/**物料必须申请*/
	@Excel(name = "物料必须申请", width = 15)
    @ApiModelProperty(value = "物料必须申请")
    private java.lang.String isApply;
	/**允许超申请入库*/
	@Excel(name = "允许超申请入库", width = 15)
    @ApiModelProperty(value = "允许超申请入库")
    private java.lang.String isApplyIn;
	/**允许零星采购*/
	@Excel(name = "允许零星采购", width = 15)
    @ApiModelProperty(value = "允许零星采购")
    private java.lang.String isScattered;
	/**允许超计划生产*/
	@Excel(name = "允许超计划生产", width = 15)
    @ApiModelProperty(value = "允许超计划生产")
    private java.lang.String isPlanProduct;
	/**允许超计划采购*/
	@Excel(name = "允许超计划采购", width = 15)
    @ApiModelProperty(value = "允许超计划采购")
    private java.lang.String isPlanIn;
	/**计划类型*/
	@Excel(name = "计划类型", width = 15, dicCode = "plan_type")
	@Dict(dicCode = "plan_type")
    @ApiModelProperty(value = "计划类型")
    private java.lang.String planType;
	/**前置天数*/
	@Excel(name = "前置天数", width = 15)
    @ApiModelProperty(value = "前置天数")
    private java.lang.Double leadDays;
	/**计划低阶码*/
	@Excel(name = "计划低阶码", width = 15)
    @ApiModelProperty(value = "计划低阶码")
    private java.lang.String planLowLeverCode;
	/**日最大供应量*/
	@Excel(name = "日最大供应量", width = 15)
    @ApiModelProperty(value = "日最大供应量")
    private java.lang.Double maximumDailySupply;
	/**最小供应量*/
	@Excel(name = "最小供应量", width = 15)
    @ApiModelProperty(value = "最小供应量")
    private java.lang.Double minimumSupply;
	/**供应参数*/
	@Excel(name = "供应参数", width = 15)
    @ApiModelProperty(value = "供应参数")
    private java.lang.String supplyParameter;
	/**安全库存*/
	@Excel(name = "安全库存", width = 15)
    @ApiModelProperty(value = "安全库存")
    private java.lang.Double safeStock;
	/**最高库存*/
	@Excel(name = "最高库存", width = 15)
    @ApiModelProperty(value = "最高库存")
    private java.lang.Double maximumStock;
	/**最低库存*/
	@Excel(name = "最低库存", width = 15)
    @ApiModelProperty(value = "最低库存")
    private java.lang.Double minimumStock;
	/**制单人*/
    @ApiModelProperty(value = "制单人")
    private java.lang.String createBy;
	/**制单日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "制单日期")
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
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private java.lang.String delFlag;
}
