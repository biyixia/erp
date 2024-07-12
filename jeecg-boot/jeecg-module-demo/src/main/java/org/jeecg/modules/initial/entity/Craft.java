package org.jeecg.modules.initial.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 工艺档案
 * @Author: jeecg-boot
 * @Date:   2023-05-03
 * @Version: V1.0
 */
@Data
@TableName("byx_craft")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="byx_craft对象", description="工艺档案")
public class Craft implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**工艺编码*/
	@Excel(name = "工艺编码", width = 15)
    @ApiModelProperty(value = "工艺编码")
    private String craftCode;
	/**工艺名称*/
	@Excel(name = "工艺名称", width = 15)
    @ApiModelProperty(value = "工艺名称")
    private String craftName;
	/**加工单位*/
	@Excel(name = "加工单位", width = 15, dicCode = "unit")
	@Dict(dicCode = "unit")
    @ApiModelProperty(value = "加工单位")
    private String craftUnit;
	/**工时单价*/
	@Excel(name = "工时单价", width = 15)
    @ApiModelProperty(value = "工时单价")
    private Double craftCost;
	/**是否委外*/
	@Excel(name = "是否委外", width = 15)
    @ApiModelProperty(value = "是否委外")
    private String isConsign;
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
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private java.lang.String delFlag;
}
