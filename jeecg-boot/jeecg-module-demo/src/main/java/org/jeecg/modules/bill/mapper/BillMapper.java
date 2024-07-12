package org.jeecg.modules.bill.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.bill.entity.Bill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 单据管理
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {
    List<Map<String,Object>> findVisitCount(@Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd);

    @Select("SELECT ROUND(SUM(apply_amount)) y\n" +
            "FROM byx_pay_detail bpd\n" +
            "JOIN byx_bill bb ON bb.id = bpd.byx_bill\n" +
            "WHERE bill_mode = '27'\n" +
            "  AND bb.del_flag = '0'\n" +
            "  AND `status` <> '0'")
    public Double getTotalSales();
    @Select("SELECT Count(*)\n" +
            "FROM byx_bill\n" +
            "WHERE bill_mode = '2' AND status <> '0'  AND del_flag = '0'")
    public Double getSalesOrder();
    @Select("SELECT Count(*)\n" +
            "FROM byx_bill\n" +
            "WHERE bill_mode = '27' AND status <> '0'  AND del_flag = '0'")
    public Double getCollect();
    @Select("SELECT Count(*)\n" +
            "FROM byx_bill\n" +
            "WHERE bill_mode = '26' AND status <> '0'  AND del_flag = '0'")
    public Double getPayment();
}
