package org.jeecg.modules.bill.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.bill.entity.*;
import org.jeecg.modules.bill.exception.ByxException;
import org.jeecg.modules.bill.exception.ExceptionCodeMsg;
import org.jeecg.modules.bill.mapper.BillingDetailMapper;
import org.jeecg.modules.bill.mapper.CollectPayPlanMapper;
import org.jeecg.modules.bill.mapper.PayDetailMapper;
import org.jeecg.modules.bill.mapper.BomRoutingMapper;
import org.jeecg.modules.bill.mapper.BillMapper;
import org.jeecg.modules.bill.service.*;
import org.jeecg.modules.bill.utils.StringUtil;
import org.jeecg.modules.initial.entity.Warehouse;
import org.jeecg.modules.initial.service.IMaterialService;
import org.jeecg.modules.initial.service.IWarehouseService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 单据管理
 * @Author: jeecg-boot
 * @Date: 2023-04-06
 * @Version: V1.0
 */
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements IBillService {

    @Autowired
    private BillMapper billMapper;
    @Autowired
    private BillingDetailMapper billingDetailMapper;
    @Autowired
    private CollectPayPlanMapper collectPayPlanMapper;
    @Autowired
    private PayDetailMapper payDetailMapper;
    @Autowired
    private BomRoutingMapper bomRoutingMapper;
    @Autowired
    private IBillingDetailService billingDetailService;
    @Autowired
    private ICollectPayPlanService collectPayPlanService;
    @Autowired
    private IPayDetailService payDetailService;
    @Autowired
    private IBomRoutingService bomRoutingService;
    @Autowired
    private ICraftDetailService craftDetailService;
    @Autowired
    private IMaterialService materialService;

    @Autowired
    private IWarehouseService warehouseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMain(Bill bill, List<BillingDetail> billingDetailList, List<CollectPayPlan> collectPayPlanList, List<PayDetail> payDetailList,
                         List<BomRouting> bomRoutingList, List<CraftDetail> craftDetailList) {
        //出库时检查库存是否足够
        if ("6".equals(bill.getBillMode())) {
            for (BillingDetail billingDetail : billingDetailList) {
                Integer inventory = billingDetailService.inventoryByWarehouseAndCode(bill.getOutWarehouseId(), billingDetail.getMaterialCode());
                Warehouse warehouse = warehouseService.getById(bill.getOutWarehouseId());
                if (billingDetail.getCount() > inventory && "N".equals(warehouse.getIsNegative())) {
                    throw new ByxException(ExceptionCodeMsg.INVENTORY_UNDERSTOCK);
                }
            }
        }
        billMapper.insert(bill);
        //根据单据保存单据明细
        billingDetailService.saveBillingDetails(bill, billingDetailList);
        collectPayPlanService.saveCollectPayPlans(bill, collectPayPlanList);
        payDetailService.savePayDetails(bill, payDetailList);
        bomRoutingService.saveBomRoutings(bill, bomRoutingList);
        craftDetailService.saveCraftDetails(bill, craftDetailList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMain(Bill bill, List<BillingDetail> billingDetailList, List<CollectPayPlan> collectPayPlanList,
                              List<PayDetail> payDetailList, List<BomRouting> bomRoutingList, List<CraftDetail> craftDetailList) {
        //出库时检查库存是否足够
        if ("6".equals(bill.getBillMode())) {
            for (BillingDetail billingDetail : billingDetailList) {
                Integer inventory = billingDetailService.inventoryByWarehouseAndCode(bill.getOutWarehouseId(), billingDetail.getMaterialCode());
                Warehouse warehouse = warehouseService.getById(bill.getOutWarehouseId());
                //修改前的数量
                BillingDetail editBefore = billingDetailService.getById(billingDetail.getId());
                if (billingDetail.getCount() - editBefore.getCount() > inventory && "N".equals(warehouse.getIsNegative())) {
                    throw new ByxException(ExceptionCodeMsg.INVENTORY_UNDERSTOCK);
                }
            }
        }
        if (!"0".equals(billMapper.selectById(bill.getId()).getStatus())) {
            throw new ByxException(ExceptionCodeMsg.INVALID_AUDIT);
        }
        billMapper.updateById(bill);
        //1.先删除子表数据
        billingDetailService.delBillingDetails(bill);
        collectPayPlanService.delCollectPayPlans(bill);
        payDetailService.delPayDetails(bill);
        bomRoutingService.delBomRoutings(bill);
        craftDetailService.delCraftDetails(bill);
        //2.子表数据重新插入
        billingDetailService.saveBillingDetails(bill, billingDetailList);
        collectPayPlanService.saveCollectPayPlans(bill, collectPayPlanList);
        payDetailService.savePayDetails(bill, payDetailList);
        bomRoutingService.saveBomRoutings(bill, bomRoutingList);
        craftDetailService.saveCraftDetails(bill, craftDetailList);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        Bill bill = billMapper.selectById(id);
        billingDetailService.delBillingDetails(bill);
        collectPayPlanService.delCollectPayPlans(bill);
        payDetailService.delPayDetails(bill);
        bomRoutingService.delBomRoutings(bill);
        craftDetailService.delCraftDetails(bill);
        //删除主表
        billMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            Bill bill = billMapper.selectById(id);
            billingDetailService.delBillingDetails(bill);
            collectPayPlanService.delCollectPayPlans(bill);
            payDetailService.delPayDetails(bill);
            bomRoutingService.delBomRoutings(bill);
            craftDetailService.delCraftDetails(bill);
            //删除主表
            billMapper.deleteById(id);
        }
    }

    @Override
    public int batchSetStatus(String status, String billIds) {
        int result = 0;
        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(billIds);
        for (Long id : ids) {
            Bill bill = billMapper.selectById(id);
            if ("0".equals(status)) {
                if ("1".equals(bill.getStatus())) {
                    bill.setStatus("0");
                    billMapper.updateById(bill);
                    result = 1;
                } else {
                    result = -1;
                }
            } else if ("1".equals(status)) {
                if ("0".equals(bill.getStatus())) {
                    bill.setStatus("1");
                    billMapper.updateById(bill);
                    result = 1;
                } else {
                    result = -1;
                }
            }
        }
        return result;
    }

    @Override
    public IPage<Bill> queryPISPageList(boolean total, String billCode, String beginTime, String endTime, int pageNo, int pageSize) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        if (oConvertUtils.isNotEmpty(billCode)) {
            queryWrapper.eq("code", billCode);
        }
        if (oConvertUtils.isNotEmpty(beginTime) && oConvertUtils.isNotEmpty(endTime)) {
            queryWrapper.between("bill_date", beginTime, endTime);
        }
        queryWrapper.eq("bill_mode", "17");

        if (total) {
            queryWrapper.notIn("status", new String[]{"0"});
        } else {
            queryWrapper.in("status", new String[]{"2", "3"});
        }
        //1.根据参数条件查询主表
        List<Bill> billList = billMapper.selectList(queryWrapper);
        //2.再根据主表的id查找所有的附表
        List<Bill> delBills = new ArrayList<>();
        for (Bill bill : billList) {
            bill.setProduct(materialService.getById(bill.getProductId()));
//            if (!total && (billingDetail.getSumInBound() >= billingDetail.getCount() || billingDetail.getSumOutBound() >= billingDetail.getCount())) {
//                billingDetails.remove(billingDetail);
//            }
        }
        Page<Bill> pageList = new Page<>();
        pageList.setRecords(billList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Override
    public List<Map<String, Object>> findSellInfo() {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, 1);
        Date dayEnd = calendar.getTime();
        calendar.add(Calendar.MONTH, -12);
        Date dayStart = calendar.getTime();
        List<Map<String, Object>> visitCount = billMapper.findVisitCount(dayStart, dayEnd);
        Date date = null;
        for (int i = 0; i < 12; i++) {
            date = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            boolean index = true;
            String str = format.format(date);
            HashMap<String, Object> m = new HashMap<>();
            for (Map<String, Object> map : visitCount) {
                if (map.get("x").equals(str)) {
                    m = (HashMap<String, Object>) map;
                    index = false;
                    break;
                }
            }
            if (index) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("x", str);
                map.put("y", 0);
                maps.add(map);
            } else {
                maps.add(m);
            }
            calendar.add(Calendar.MONTH, 1);
        }
        return maps;
    }

    @Override
    public Double getTotalSales() {
        return billMapper.getTotalSales();
    }

    @Override
    public Double getCollect() {
        return billMapper.getCollect();
    }

    @Override
    public Double getPayment() {
        return billMapper.getPayment();
    }

    @Override
    public Double getSalesOrder() {
        return billMapper.getSalesOrder();
    }
}
