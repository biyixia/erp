package org.jeecg.modules.initial.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @Description: 客户补充信息
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
@ApiModel(value="byx_customer对象", description="客户补充信息")
@Data
@TableName("byx_customer")
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**客户等级*/
	@Excel(name = "客户等级", width = 15, dicCode = "level")
	@Dict(dicCode = "level")
    @ApiModelProperty(value = "客户等级")
    private java.lang.String level;
	/**信用额度*/
	@Excel(name = "信用额度", width = 15)
    @ApiModelProperty(value = "信用额度")
    private java.lang.String creditLine;
	/**应收余额*/
	@Excel(name = "应收余额", width = 15)
    @ApiModelProperty(value = "应收余额")
    private java.lang.String arbMoney;
	/**应付余额*/
	@Excel(name = "应付余额", width = 15)
    @ApiModelProperty(value = "应付余额")
    private java.lang.String apbMoney;
	/**信用余额*/
	@Excel(name = "信用余额", width = 15)
    @ApiModelProperty(value = "信用余额")
    private java.lang.String balanceMoney;
	/**发票邮件*/
	@Excel(name = "发票邮件", width = 15)
    @ApiModelProperty(value = "发票邮件")
    private java.lang.String taxEmail;
	/**登记税务名称*/
	@Excel(name = "登记税务名称", width = 15)
    @ApiModelProperty(value = "登记税务名称")
    private java.lang.String texName;
	/**客商id*/
    @ApiModelProperty(value = "客商id")
    private java.lang.String merchantId;
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private java.lang.String delFlag;
}
