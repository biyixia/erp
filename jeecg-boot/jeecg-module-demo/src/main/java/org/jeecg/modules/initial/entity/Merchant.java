package org.jeecg.modules.initial.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 客商管理
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
@ApiModel(value="byx_merchant对象", description="客商管理")
@Data
@TableName("byx_merchant")
public class Merchant implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**客商编号*/
	@Excel(name = "客商编号", width = 15)
    @ApiModelProperty(value = "客商编号")
    private java.lang.String code;
	/**客商名称*/
	@Excel(name = "客商名称", width = 15)
    @ApiModelProperty(value = "客商名称")
    private java.lang.String name;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @ApiModelProperty(value = "联系人")
    private java.lang.String contacts;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phoneNum;
	/**所属地区*/
	@Excel(name = "所属地区", width = 15)
    @ApiModelProperty(value = "所属地区")
    private java.lang.String area;
	/**详细地址*/
	@Excel(name = "详细地址", width = 15)
    @ApiModelProperty(value = "详细地址")
    private java.lang.String address;
	/**统一信用证号*/
	@Excel(name = "统一信用证号", width = 15)
    @ApiModelProperty(value = "统一信用证号")
    private java.lang.String creditNum;
	/**币别*/
	@Excel(name = "币别", width = 15, dicCode = "currency")
    @Dict(dicCode = "currency")
    @ApiModelProperty(value = "币别")
    private java.lang.String currency;
	/**QQ*/
	@Excel(name = "QQ", width = 15)
    @ApiModelProperty(value = "QQ")
    private java.lang.String qq;
	/**微信*/
	@Excel(name = "微信", width = 15)
    @ApiModelProperty(value = "微信")
    private java.lang.String wechat;
	/**电子邮箱*/
	@Excel(name = "电子邮箱", width = 15)
    @ApiModelProperty(value = "电子邮箱")
    private java.lang.String email;
	/**传真*/
	@Excel(name = "传真", width = 15)
    @ApiModelProperty(value = "传真")
    private java.lang.String fax;
	/**手机号码*/
	@Excel(name = "手机号码", width = 15)
    @ApiModelProperty(value = "手机号码")
    private java.lang.String telephone;
	/**所属银行*/
	@Excel(name = "所属银行", width = 15, dicCode = "bank_type")
    @Dict(dicCode = "bank_type")
    @ApiModelProperty(value = "所属银行")
    private java.lang.String bankType;
	/**开户行*/
	@Excel(name = "开户行", width = 15)
    @ApiModelProperty(value = "开户行")
    private java.lang.String bankName;
	/**联行号*/
	@Excel(name = "联行号", width = 15)
    @ApiModelProperty(value = "联行号")
    private java.lang.String bankNum;
	/**银行账号*/
	@Excel(name = "银行账号", width = 15)
    @ApiModelProperty(value = "银行账号")
    private java.lang.String bankAccount;
	/**纳税人识别号*/
	@Excel(name = "纳税人识别号", width = 15)
    @ApiModelProperty(value = "纳税人识别号")
    private java.lang.String taxNum;
	/**税率*/
	@Excel(name = "税率", width = 15)
    @ApiModelProperty(value = "税率")
    private java.lang.Double taxRate;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private java.lang.String sorter;
	/**是否客户*/
	@Excel(name = "是否客户", width = 15)
    @ApiModelProperty(value = "是否客户")
    private java.lang.String isCustomer;
	/**是否供应商*/
	@Excel(name = "是否供应商", width = 15)
    @ApiModelProperty(value = "是否供应商")
    private java.lang.String isSupplier;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String description;
	/**启用*/
	@Excel(name = "启用", width = 15)
    @ApiModelProperty(value = "启用")
    private java.lang.String enabled;
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
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private java.lang.String delFlag;
}
