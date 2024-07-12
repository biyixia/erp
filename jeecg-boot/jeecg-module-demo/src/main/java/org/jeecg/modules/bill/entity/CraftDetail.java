package org.jeecg.modules.bill.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.modules.initial.entity.Craft;
import org.jeecg.modules.initial.entity.Material;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 工艺明细
 * @Author: jeecg-boot
 * @Date:   2023-05-04
 * @Version: V1.0
 */
@ApiModel(value="byx_craft_detail对象", description="工艺明细")
@Data
@TableName("byx_craft_detail")
public class CraftDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**工艺名称*/
	@Excel(name = "工艺名称", width = 15)
    @ApiModelProperty(value = "工艺名称")
    private String craftCode;
    @TableField(exist = false)
    private Craft craft;
	/**完工数量*/
	@Excel(name = "完工数量", width = 15)
    @ApiModelProperty(value = "完工数量")
    private Double completeQuantity;
	/**料废数量*/
	@Excel(name = "料废数量", width = 15)
    @ApiModelProperty(value = "料废数量")
    private Double scrapQuantity;
	/**工废数量*/
	@Excel(name = "工废数量", width = 15)
    @ApiModelProperty(value = "工废数量")
    private Double wasteQuantity;
	/**来源*/
	@Excel(name = "来源", width = 15)
    @ApiModelProperty(value = "来源")
    private String source;
	/**来源单号*/
	@Excel(name = "来源单号", width = 15)
    @ApiModelProperty(value = "来源单号")
    private String sourceNumber;
    /**加工金额*/
    @Excel(name = "加工金额", width = 15)
    @ApiModelProperty(value = "加工金额")
    private Double craftAmount;
    /**工废扣款*/
    @Excel(name = "工废扣款", width = 15)
    @ApiModelProperty(value = "工废扣款")
    private Double wasteAmount;
    /**合计金额*/
    @Excel(name = "合计金额", width = 15)
    @ApiModelProperty(value = "合计金额")
    private Double totalAmount;
    /**委托数量*/
    @Excel(name = "委托数量", width = 15)
    @ApiModelProperty(value = "委托数量")
    private Double consignQuantity;
    /**累计加工入库数量*/
    @Excel(name = "累计加工入库数量", width = 15)
    @ApiModelProperty(value = "累计加工入库数量")
    private Double processInQuantity;
    /**累计废品入库数量*/
    @Excel(name = "累计废品入库数量", width = 15)
    @ApiModelProperty(value = "累计废品入库数量")
    private Double wasteInQuantity;
    /**累计加工结算数量*/
    @Excel(name = "累计加工结算数量", width = 15)
    @ApiModelProperty(value = "累计加工结算数量")
    private Double processSettleQuantity;
    /**累计废品结算数量*/
    @Excel(name = "累计废品结算数量", width = 15)
    @ApiModelProperty(value = "累计废品结算数量")
    private Double wasteSettleQuantity;
    /**税率*/
    @Excel(name = "税率", width = 15)
    @ApiModelProperty(value = "税率")
    private java.lang.Double rate;
    /**无税金额*/
    @Excel(name = "无税金额", width = 15)
    @ApiModelProperty(value = "无税金额")
    private java.lang.Double amount;
    /**税额*/
    @Excel(name = "税额", width = 15)
    @ApiModelProperty(value = "税额")
    private java.lang.Double tax;
    /**产品Code*/
    @Excel(name = "产品Code", width = 15)
    @ApiModelProperty(value = "产品Code")
    private String productCode;
    @TableField(exist = false)
    private Material product;

	/**单据id*/
    @ApiModelProperty(value = "单据id")
    private String billId;

    private String referId;
    private String bomRoutingId;

    @TableField(exist = false)
    private BomRouting bomRouting;
    @TableField(exist = false)
    private Bill bill;
}
