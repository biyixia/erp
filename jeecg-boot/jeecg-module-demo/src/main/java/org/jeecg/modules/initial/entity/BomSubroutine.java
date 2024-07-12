package org.jeecg.modules.initial.entity;

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
 * @Description: 子件明细
 * @Author: jeecg-boot
 * @Date:   2023-05-01
 * @Version: V1.0
 */
@ApiModel(value="byx_bom_subroutine对象", description="子件明细")
@Data
@TableName("byx_bom_subroutine")
public class BomSubroutine implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**物料编码*/
	@Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private String code;
    @TableField(exist = false)
    private Material material;
	/**物料名称*/
	@Excel(name = "物料名称", width = 15)
    @ApiModelProperty(value = "物料名称")
    private String materialName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String size;
	/**计量单位*/
	@Excel(name = "计量单位", width = 15)
    @ApiModelProperty(value = "计量单位")
    private String unit;
	/**件装量*/
	@Excel(name = "件装量", width = 15)
    @ApiModelProperty(value = "件装量")
    private String unitLoad;
	/**分子用量*/
	@Excel(name = "分子用量", width = 15)
    @ApiModelProperty(value = "分子用量")
    private Double numerator;
	/**分母用量*/
	@Excel(name = "分母用量", width = 15)
    @ApiModelProperty(value = "分母用量")
    private Double denominator;
	/**提前天数*/
	@Excel(name = "提前天数", width = 15)
    @ApiModelProperty(value = "提前天数")
    private Double advenceDays;
	/**产出平*/
	@Excel(name = "产出品", width = 15)
    @ApiModelProperty(value = "产出品")
    private String outputLevel;
	/**足额配料*/
	@Excel(name = "足额配料", width = 15)
    @ApiModelProperty(value = "足额配料")
    private String fullIngredients;
	/**物料清单维护id*/
    @ApiModelProperty(value = "物料清单维护id")
    private String bomId;
}
