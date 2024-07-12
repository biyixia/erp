package org.jeecg.modules.voucher.entity;

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
 * @Description: 凭证模板
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@ApiModel(value="byx_certificate_template对象", description="凭证模板")
@Data
@TableName("byx_certificate_template")
public class CertificateTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
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
    private Date billDate;
	/**凭证类别*/
	@Excel(name = "凭证类别", width = 15, dicCode = "voucher_type")
    @Dict(dicCode = "voucher_type")
    @ApiModelProperty(value = "凭证类别")
    private String voucherType;
	/**单据类型*/
	@Excel(name = "单据类型", width = 15, dicCode = "bill_type")
    @Dict(dicCode = "bill_type")
    @ApiModelProperty(value = "单据类型")
    private String billType;
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
