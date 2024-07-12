package org.jeecg.modules.initial.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
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
import java.io.UnsupportedEncodingException;

/**
 * @Description: 会计科目
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@Data
@TableName("byx_accounting_subject")
@ApiModel(value="byx_accounting_subject对象", description="会计科目")
public class AccountingSubject implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**科目编码*/
	@Excel(name = "科目编码", width = 15)
    @ApiModelProperty(value = "科目编码")
    private String subCode;
	/**科目名称*/
	@Excel(name = "科目名称", width = 15)
    @ApiModelProperty(value = "科目名称")
    private String subName;
	/**上级科目编码*/
	@Excel(name = "上级科目编码", width = 15)
    @ApiModelProperty(value = "上级科目编码")
    private String pid;
	/**余额方向*/
	@Excel(name = "余额方向", width = 15, dicCode = "sub_property")
	@Dict(dicCode = "sub_property")
    @ApiModelProperty(value = "余额方向")
    private String subProperty;
	/**部门核算*/
	@Excel(name = "部门核算", width = 15)
    @ApiModelProperty(value = "部门核算")
    private String isDepcheck;
	/**人员核算*/
	@Excel(name = "人员核算", width = 15)
    @ApiModelProperty(value = "人员核算")
    private String isEmpcheck;
	/**科目类型*/
	@Excel(name = "科目类型", width = 15, dicCode = "subclass")
	@Dict(dicCode = "subclass")
    @ApiModelProperty(value = "科目类型")
    private String subclass;
	/**客商核算*/
	@Excel(name = "客商核算", width = 15)
    @ApiModelProperty(value = "客商核算")
    private String isCuscheck;
	/**现金流量*/
	@Excel(name = "现金流量", width = 15)
    @ApiModelProperty(value = "现金流量")
    private String isCashflow;
	/**日记账*/
	@Excel(name = "日记账", width = 15)
    @ApiModelProperty(value = "日记账")
    private String isDiary;
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
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private java.lang.String delFlag;
}
