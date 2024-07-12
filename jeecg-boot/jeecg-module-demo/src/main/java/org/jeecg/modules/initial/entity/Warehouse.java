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
 * @Description: 仓库档案
 * @Author: jeecg-boot
 * @Date:   2023-04-29
 * @Version: V1.0
 */
@ApiModel(value="byx_warehouse对象", description="仓库档案")
@Data
@TableName("byx_warehouse")
public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
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
	/**允许负数*/
	@Excel(name = "允许负数", width = 15)
    @ApiModelProperty(value = "允许负数")
    private String isNegative;
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
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private java.lang.String delFlag;
}
