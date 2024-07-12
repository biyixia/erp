package org.jeecg.modules.bill.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;
import org.jeecg.modules.initial.entity.Material;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 标的明细
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@ApiModel(value="byx_billing_detail对象", description="标的明细")
@Data
@TableName("byx_billing_detail")
public class BillingDetail implements Serializable,Comparable<BillingDetail> {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**物料编号*/
    @Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private java.lang.String materialCode;
    /**数量*/
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Double count;
    /**含税金额*/
    @Excel(name = "含税金额", width = 15)
    @ApiModelProperty(value = "含税金额")
    private java.lang.Double taxIncludedAmount;
    /**无税金额*/
    @Excel(name = "无税金额", width = 15)
    @ApiModelProperty(value = "无税金额")
    private java.lang.Double amount;
	/**税额*/
	@Excel(name = "税额", width = 15)
    @ApiModelProperty(value = "税额")
    private java.lang.Double tax;
	/**件数*/
	@Excel(name = "件数", width = 15)
    @ApiModelProperty(value = "件数")
    private java.lang.Double number;
    /**散件*/
    @Excel(name = "散件", width = 15)
    @ApiModelProperty(value = "散件")
    private java.lang.Double looseItem;
	/**预计发货日期*/
	@Excel(name = "预计发货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "预计发货日期")
    private java.util.Date expectedDelivery;
	/**预计付款日期*/
	@Excel(name = "预计付款日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "预计付款日期")
    private java.util.Date expectedPayment;
	/**来源*/
	@Excel(name = "来源", width = 15)
    @ApiModelProperty(value = "来源")
    private java.lang.String source;
	/**来源单号*/
	@Excel(name = "来源单号", width = 15)
    @ApiModelProperty(value = "来源单号")
    private java.lang.String sourceNumber;
	/**单据id*/
    @ApiModelProperty(value = "单据id")
    private java.lang.String billId;
    /**累计入库数量*/
    @ApiModelProperty(value = "累计入库数量")
    private java.lang.Double sumInBound;
    /**累计出库数量*/
    @ApiModelProperty(value = "累计出库数量")
    private java.lang.Double sumOutBound;
    /**分子数量*/
    @ApiModelProperty(value = "分子数量")
    private java.lang.Double numerator;
    /**分母数量*/
    @ApiModelProperty(value = "分母数量")
    private java.lang.Double denominator;
    /**标准用量*/
    @ApiModelProperty(value = "标准用量")
    private java.lang.Double standardDosage;
    /**需求日期*/
    @Excel(name = "需求日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private java.util.Date demandedDate;
    /**是否产出品*/
    @ApiModelProperty(value = "是否产出品")
    private java.lang.String isOutput;
    /**是否足额配料*/
    @ApiModelProperty(value = "是否足额配料")
    private java.lang.String fullIngredients;
    //引用id，用于参照其他表
    private String referId;
    //物料详情
    @TableField(exist = false)
    private Material material;
    @TableField(exist = false)
    private Bill bill;

    @Override
    public int compareTo(@NotNull BillingDetail o) {
        return  this.getDemandedDate().compareTo(o.getDemandedDate());
    }
}
