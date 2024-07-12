package org.jeecg.modules.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.BillingDetail;
import org.jeecg.modules.bill.entity.CollectPayPlan;
import org.jeecg.modules.bill.mapper.BillMapper;
import org.jeecg.modules.bill.mapper.CollectPayPlanMapper;
import org.jeecg.modules.bill.service.ICollectPayPlanService;
import org.jeecg.modules.initial.mapper.MerchantMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 款计划
 * @Author: jeecg-boot
 * @Date: 2023-04-06
 * @Version: V1.0
 */
@Service
public class CollectPayPlanServiceImpl extends ServiceImpl<CollectPayPlanMapper, CollectPayPlan> implements ICollectPayPlanService {

    @Autowired
    private CollectPayPlanMapper collectPayPlanMapper;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public void saveCollectPayPlans(Bill bill, List<CollectPayPlan> collectPayPlanList) {
        if (collectPayPlanList != null && collectPayPlanList.size() > 0) {
            for (CollectPayPlan entity : collectPayPlanList) {
                //外键设置
                entity.setBillId(bill.getId());
                collectPayPlanMapper.insert(entity);
            }
        }
    }

    @Override
    public void delCollectPayPlans(Bill bill) {
        collectPayPlanMapper.deleteByMainId(bill.getId());
    }

    @Override
    public List<CollectPayPlan> selectByMainId(String mainId) {
        return collectPayPlanMapper.selectByMainId(mainId);
    }

    @Override
    public IPage<CollectPayPlan> queryPoPayPageList(boolean total, String type, String billCode, String clientId, String materialParam, String beginTime, String endTime, int pageSize, int pageNo) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        if (oConvertUtils.isNotEmpty(billCode)) {
            queryWrapper.eq("code", billCode);
        }
        if (oConvertUtils.isNotEmpty(clientId)) {
            queryWrapper.eq("client_id", clientId);
        }
        if (oConvertUtils.isNotEmpty(beginTime) && oConvertUtils.isNotEmpty(endTime)) {
            queryWrapper.between("bill_date", beginTime, endTime);
        }
        queryWrapper.eq("bill_mode", type);
        queryWrapper.eq("payment_style", "按条款");
        queryWrapper.in("status", new String[]{"1", "2", "3"});
        //1.根据参数条件查询主表
        List<Bill> billList = billMapper.selectList(queryWrapper);
        //2.再根据主表的id查找所有的附表
        List<CollectPayPlan> collectPayPlanList = new ArrayList<>();
        for (Bill bill : billList) {
            bill.setMerchant(merchantMapper.selectById(bill.getClientId()));
            List<CollectPayPlan> collectPayPlans = this.selectByMainId(bill.getId());
            for (CollectPayPlan collectPayPlan : collectPayPlans) {
                collectPayPlan.setBill(bill);
            }
            ArrayList<CollectPayPlan> cpps = new ArrayList<>();
            for (CollectPayPlan collectPayPlan : collectPayPlans) {
                if (!total && collectPayPlan.getSumReqAmount() >= collectPayPlan.getPayAmount()) {
                    cpps.add(collectPayPlan);
                }
            }
            collectPayPlanList.addAll(collectPayPlans);
            collectPayPlanList.removeAll(cpps);
        }
        Page<CollectPayPlan> pageList = new Page<>();
        pageList.setRecords(collectPayPlanList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }
}
