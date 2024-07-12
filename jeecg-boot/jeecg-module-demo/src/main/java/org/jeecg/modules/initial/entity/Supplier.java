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
 * @Description: 供应商补充信息
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
@ApiModel(value="byx_supplier对象", description="供应商补充信息")
@Data
@TableName("byx_supplier")
public class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    private java.lang.String depName;
	/**部门业务员*/
	@Excel(name = "部门业务员", width = 15)
    @ApiModelProperty(value = "部门业务员")
    private java.lang.String depPerson;
	/**客商id*/
    @ApiModelProperty(value = "客商id")
    private java.lang.String merchantId;
    /**
     * 删除状态（0，正常，1已删除）
     */
    @TableLogic
    private java.lang.String delFlag;
}
