package org.jeecg.modules.initial.vo;

import java.util.List;
import org.jeecg.modules.initial.entity.Warehouse;
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
 * @Description: 仓库档案
 * @Author: jeecg-boot
 * @Date:   2023-04-29
 * @Version: V1.0
 */
@Data
@ApiModel(value="byx_warehousePage对象", description="仓库档案")
public class WarehousePage {

	/**主键*/
	@ApiModelProperty(value = "主键")
    private String id;
	/**仓库编码*/
	@Excel(name = "仓库编码", width = 15)
	@ApiModelProperty(value = "仓库编码")
    private String code;
	/**仓库名称*/
	@Excel(name = "仓库名称", width = 15)
	@ApiModelProperty(value = "仓库名称")
    private String warehouseName;
	/**物权归属*/
	@Excel(name = "物权归属", width = 15, dicCode = "ownership")
    @Dict(dicCode = "ownership")
	@ApiModelProperty(value = "物权归属")
    private String ownership;
	/**计价方式*/
	@Excel(name = "计价方式", width = 15, dicCode = "price_type")
    @Dict(dicCode = "price_type")
	@ApiModelProperty(value = "计价方式")
    private String priceType;
	/**允许负数*/
	@Excel(name = "允许负数", width = 15)
	@ApiModelProperty(value = "允许负数")
    private String isNegative;
	/**计算成本*/
	@Excel(name = "计算成本", width = 15)
	@ApiModelProperty(value = "计算成本")
    private String isCost;
	/**参与ROP运算*/
	@Excel(name = "参与ROP运算", width = 15)
	@ApiModelProperty(value = "参与ROP运算")
    private String isRop;
	/**参与MRP运算*/
	@Excel(name = "参与MRP运算", width = 15)
	@ApiModelProperty(value = "参与MRP运算")
    private String isMrp;
	/**科目名称*/
	@Excel(name = "科目名称", width = 15)
	@ApiModelProperty(value = "科目名称")
    private String subjectId;
	/**制单人*/
	@ApiModelProperty(value = "制单人")
    private String createBy;
	/**制单日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "制单日期")
    private Date createTime;
	/**关闭人*/
	@Excel(name = "关闭人", width = 15)
	@ApiModelProperty(value = "关闭人")
    private String closeBy;
	/**关闭日期*/
	@Excel(name = "关闭日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "关闭日期")
    private Date closeTime;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	
	
}
