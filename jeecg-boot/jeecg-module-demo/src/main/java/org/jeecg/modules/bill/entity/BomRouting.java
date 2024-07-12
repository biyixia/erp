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
 * @Description: 工艺线路
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@ApiModel(value="byx_bom_routing对象", description="工艺线路")
@Data
@TableName("byx_bom_routing")
public class BomRouting implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**工序编码*/
	@Excel(name = "工序编码", width = 15)
    @ApiModelProperty(value = "工序编码")
    private java.lang.String craftCode;
	/**工序名称*/
	@Excel(name = "工序名称", width = 15)
    @ApiModelProperty(value = "工序名称")
    private java.lang.String craftName;
	/**加工单位*/
	@Excel(name = "加工单位", width = 15)
    @ApiModelProperty(value = "加工单位")
    private java.lang.String craftUnit;
	/**加工工时*/
	@Excel(name = "加工工时", width = 15)
    @ApiModelProperty(value = "加工工时")
    private java.lang.Double craftTime;
	/**工时单价*/
	@Excel(name = "工时单价", width = 15)
    @ApiModelProperty(value = "工时单价")
    private java.lang.Double craftCost;
	/**加工单价*/
	@Excel(name = "加工单价", width = 15)
    @ApiModelProperty(value = "加工单价")
    private java.lang.Double processCost;
	/**料废单价*/
	@Excel(name = "料废单价", width = 15)
    @ApiModelProperty(value = "料废单价")
    private java.lang.Double wastePrice;
	/**工费单价*/
	@Excel(name = "工费单价", width = 15)
    @ApiModelProperty(value = "工费单价")
    private java.lang.Double wagePrice;
	/**顺序加工*/
	@Excel(name = "顺序加工", width = 15)
    @ApiModelProperty(value = "顺序加工")
    private java.lang.String isOrder;
	/**委托加工*/
	@Excel(name = "委托加工", width = 15)
    @ApiModelProperty(value = "委托加工")
    private java.lang.String isConsign;
	/**序号*/
	@Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
    private java.lang.String serialNumber;
	/**单据id*/
    @ApiModelProperty(value = "单据id")
    private java.lang.String billId;
    @TableField(exist = false)
    private Bill bill;
    /**bomId*/
    @ApiModelProperty(value = "bomId")
    private java.lang.String bomId;
    //完工数量
    private Double completeCount;
    //委托数量
    private Double consignCount;
}
