package org.jeecg.modules.bill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.BillingDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 标的明细
 * @Author: jeecg-boot
 * @Date: 2023-04-06
 * @Version: V1.0
 */
public interface IBillingDetailService extends IService<BillingDetail> {

    /**
     * 通过主表id查询子表数据
     *
     * @param mainId 主表id
     * @return List<BillingDetail>
     */
    public List<BillingDetail> selectByMainId(String mainId);



    /**
     * 参照单据数据查询
     */
    IPage<BillingDetail> queryPoPageList(boolean total, String type, String billCode, String clientId, String materialParam, String beginTime, String endTime, int pageNo, int pageSize, String inWarehouseId, String outWarehouseId);

    /**
     * 查询库存现存量
     */
    IPage<BillingDetail> queryInventory(String materialParam, int pageNo, int pageSize, String inWarehouseId, String outWarehouseId);

    /**
     * 执行ROP运算
     */
    IPage<BillingDetail> queryROP(String materialParam, int pageNo, int pageSize, String inWarehouseId, String outWarehouseId);

    /**
     * 执行MPS运算
     */
    IPage<Bill> queryMPS(int pageNo, int pageSize);

    /**
     * 执行MRP运算
     */
    IPage<BillingDetail> queryMRP(int pageNo, int pageSize);

    /**
     * 采购参照MRP计划
     */
    IPage<BillingDetail> queryPurchaseMRP(int pageNo, int pageSize);

    /**
     * 根据单据保存单据明细
     */
    public void saveBillingDetails(Bill bill, List<BillingDetail> billingDetailList);

    /**
     * 根据单据删除单据明细
     */
    public void delBillingDetails(Bill bill);

    /**
     * ROP查询
     */
    IPage<BillingDetail> queryROPList(String materialCode, int pageNo, int pageSize);

    /**
     * MPS查询
     */
    IPage<BillingDetail> queryMPSList(String materialCode, int pageNo, int pageSize);    /**
     * MPS查询
     */
    int inventoryByWarehouseAndCode(String warehouseId, String code);
}
