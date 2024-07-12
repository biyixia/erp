package org.jeecg.modules.initial.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.bill.entity.Inventory;
import org.jeecg.modules.initial.entity.Bom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 物料清单
 * @Author: jeecg-boot
 * @Date: 2023-05-01
 * @Version: V1.0
 */
@Mapper
public interface BomMapper extends BaseMapper<Bom> {
    //根据产品id查询默认BOM的数量
    @Select("SELECT count(*) FROM `byx_bom`WHERE prod_id =  #{prodId} AND is_default='Y' AND del_flag = '0'")
    public int getDefaultByProd(@Param("prodId") String prodId);
}
