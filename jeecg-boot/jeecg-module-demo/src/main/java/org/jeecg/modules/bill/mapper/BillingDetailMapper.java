package org.jeecg.modules.bill.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.BillingDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.bill.entity.Inventory;

/**
 * @Description: 标的明细
 * @Author: jeecg-boot
 * @Date: 2023-04-06
 * @Version: V1.0
 */
@Mapper
public interface BillingDetailMapper extends BaseMapper<BillingDetail> {

    /**
     * 通过主表id删除子表数据
     *
     * @param mainId 主表id
     * @return boolean
     */
    public boolean deleteByMainId(@Param("mainId") String mainId);

    /**
     * 通过主表id查询子表数据
     *
     * @param mainId 主表id
     * @return List<BillingDetail>
     */
    public List<BillingDetail> selectByMainId(@Param("mainId") String mainId);
    @Select("SELECT\n" +
            "in_warehouse_id,material_code,SUM(count) sumCount\n" +
            "FROM(\n" +
            "SELECT bb.in_warehouse_id,bd.material_code,bd.count,bb.`status`\n" +
            "FROM\n" +
            "byx_bill bb\n" +
            "JOIN byx_billing_detail bd WHERE bb.id = bd.bill_id AND bill_mode IN(2,12,20,10,11)\n" +
            "UNION ALL\n" +
            "SELECT bb.out_warehouse_id,bd.material_code,-bd.count,bb.`status`\n" +
            "FROM\n" +
            "byx_bill bb\n" +
            "JOIN byx_billing_detail bd WHERE bb.id = bd.bill_id AND bill_mode IN(6,15,19,9)\n" +
            ")a\n" +
            "GROUP BY in_warehouse_id,material_code")
    public List<Inventory> queryInventory();

    @Select("SELECT\n" +
            "\tmaterial_code,SUM(count) sumCount\n" +
            "FROM(\n" +
            "\tSELECT bd.material_code,bd.count\n" +
            "\tFROM\n" +
            "\tbyx_bill bb\n" +
            "\tJOIN byx_billing_detail bd WHERE bb.id = bd.bill_id AND bill_mode IN(2,12,20,10)\n" +
            "\tUNION ALL\n" +
            "\tSELECT bd.material_code,-bd.count\n" +
            "\tFROM\n" +
            "\tbyx_bill bb\n" +
            "\tJOIN byx_billing_detail bd WHERE bb.id = bd.bill_id AND bill_mode IN(6,15,19,9)\n" +
            ")a\n" +
            "WHERE material_code = #{materialCode}"+
            "GROUP BY material_code")
    public List<Inventory> materialInventory(@Param("materialCode") String materialCode);
    @Select("SELECT\n" +
            "SUM(count) sumCount\n" +
            "FROM(\n" +
            "SELECT bb.in_warehouse_id,bd.material_code,bd.count,bb.`status`\n" +
            "FROM\n" +
            "byx_bill bb\n" +
            "JOIN byx_billing_detail bd WHERE bb.id = bd.bill_id AND bill_mode IN(2,12,20,10)\n" +
            "UNION ALL\n" +
            "SELECT bb.out_warehouse_id,bd.material_code,-bd.count,bb.`status`\n" +
            "FROM\n" +
            "byx_bill bb\n" +
            "JOIN byx_billing_detail bd WHERE bb.id = bd.bill_id AND bill_mode IN(6,15,19,9)\n" +
            ")a\n" +
            "WHERE in_warehouse_id=#{warehouseId} AND material_code = #{code}\n" +
            "GROUP BY in_warehouse_id,material_code")
    public Integer inventoryByWarehouseAndCode(@Param("warehouseId") String warehouseId,@Param("code") String code);
}
