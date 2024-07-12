package org.jeecg.modules.bill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.bill.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 单据管理
 * @Author: jeecg-boot
 * @Date: 2023-04-06
 * @Version: V1.0
 */
public interface IBillService extends IService<Bill> {



    /**
     * 添加一对多
     *
     * @param bill
     * @param billingDetailList
     * @param collectPayPlanList
     * @param payDetailList
     * @param bomRoutingList
     */
    public void saveMain(Bill bill, List<BillingDetail> billingDetailList, List<CollectPayPlan> collectPayPlanList, List<PayDetail> payDetailList, List<BomRouting> bomRoutingList,List<CraftDetail> craftDetailList);

    /**
     * 修改一对多
     *
     * @param bill
     * @param billingDetailList
     * @param collectPayPlanList
     * @param payDetailList
     * @param bomRoutingList
     */
    public void updateMain(Bill bill, List<BillingDetail> billingDetailList, List<CollectPayPlan> collectPayPlanList, List<PayDetail> payDetailList,List<BomRouting> bomRoutingList,List<CraftDetail> craftDetailList);

    /**
     * 删除一对多
     *
     * @param id
     */
    public void delMain(String id);

    /**
     * 批量删除一对多
     *
     * @param idList
     */
    public void delBatchMain(Collection<? extends Serializable> idList);

    /**
     * 批量设置审核或反审核
     */
    public int batchSetStatus(String status, String billIds);
    /**
     * 参照生产订单入库
     */
    IPage<Bill> queryPISPageList(boolean total, String billCode, String beginTime, String endTime, int pageNo, int pageSize);
    /**
     *   首页：根据时间统计
     * @return
     */
    List<Map<String,Object>> findSellInfo();
    Double getTotalSales();

    Double getCollect();

    Double getPayment();
    Double getSalesOrder();
}
