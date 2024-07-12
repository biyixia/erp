package org.jeecg.modules.initial.vo;

import java.util.List;

import org.jeecg.modules.bill.entity.BomRouting;
import org.jeecg.modules.initial.entity.Bom;
import org.jeecg.modules.initial.entity.BomSubroutine;
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
 * @Description: 物料清单
 * @Author: jeecg-boot
 * @Date:   2023-05-01
 * @Version: V1.0
 */
@Data
@ApiModel(value="byx_bomPage对象", description="物料清单")
public class BomPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
    private String id;
	/**单据编码*/
	@Excel(name = "单据编码", width = 15)
	@ApiModelProperty(value = "单据编码")
    private String code;
	/**单据日期*/
	@Excel(name = "单据日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "单据日期")
    private Date billsDate;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
	@ApiModelProperty(value = "产品名称")
    private String prodId;
	/**BOM名称*/
	@Excel(name = "BOM名称", width = 15)
	@ApiModelProperty(value = "BOM名称")
    private String bomName;
	/**是否默认*/
	@Excel(name = "是否默认", width = 15)
	@ApiModelProperty(value = "是否默认")
    private String isDefault;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
    private String remark;
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
	
	@ExcelCollection(name="子件明细")
	@ApiModelProperty(value = "子件明细")
	private List<BomSubroutine> bomSubroutineList;

	@ExcelCollection(name="工艺线路")
	@ApiModelProperty(value = "工艺线路")
	private List<BomRouting> bomRoutingList;
	
}
