package org.jeecg.modules.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.BillingDetail;
import org.jeecg.modules.bill.entity.BomRouting;
import org.jeecg.modules.bill.mapper.BillMapper;
import org.jeecg.modules.bill.mapper.BomRoutingMapper;
import org.jeecg.modules.bill.service.IBomRoutingService;
import org.jeecg.modules.initial.mapper.MaterialMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 工艺线路
 * @Author: jeecg-boot
 * @Date: 2023-04-06
 * @Version: V1.0
 */
@Service
public class BomRoutingServiceImpl extends ServiceImpl<BomRoutingMapper, BomRouting> implements IBomRoutingService {

    @Autowired
    private BomRoutingMapper bomRoutingMapper;
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private MaterialMapper materialMapper;

    @Override
    public IPage<BomRouting> queryBomRoutingList(boolean total, String type, String consign, String billCode, String productId, String beginTime, String endTime, int pageNo, int pageSize) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        if (oConvertUtils.isNotEmpty(billCode)) {
            queryWrapper.eq("code", billCode);
        }
        if (oConvertUtils.isNotEmpty(productId)) {
            queryWrapper.eq("product_id", productId);
        }
        if (oConvertUtils.isNotEmpty(beginTime) && oConvertUtils.isNotEmpty(endTime)) {
            queryWrapper.between("bill_date", beginTime, endTime);
        }
        queryWrapper.eq("bill_mode", type);
        if (total) {
            queryWrapper.notIn("status", new String[]{"0"});
        } else {
            queryWrapper.in("status", new String[]{"1", "2", "3", "4"});
        }


        //1.根据参数条件查询主表
        List<Bill> billList = billMapper.selectList(queryWrapper);
        //2.再根据主表的id查找所有的附表
        List<BomRouting> bomRoutingList = new ArrayList<>();
        for (Bill bill : billList) {
            bill.setProduct(materialMapper.selectById(bill.getProductId()));
            List<BomRouting> bomRoutings = this.selectByMainId(bill.getId());
            List<BomRouting> delBr = new ArrayList<>();
            for (BomRouting bomRouting : bomRoutings) {
                bomRouting.setBill(bill);
                //若为顺序加工，查询上一步骤是否完成，若不完成则删除
                if (!total) {
                    if (bomRouting.getIsOrder().equals("Y") && !"1".equals(bomRouting.getSerialNumber())) {
                        BomRouting br = bomRoutingMapper.selectOne(new QueryWrapper<BomRouting>().eq("serial_number", Integer.parseInt(bomRouting.getSerialNumber()) - 1).eq("bill_id", bill.getId()));
                        if ((br.getIsConsign().equals("Y") && br.getConsignCount() <= 0) || (br.getIsConsign().equals("N") && br.getCompleteCount() <= 0)) {
                            delBr.add(bomRouting);
                        }
                    }
                }
                switch (consign) {
                    case "0":
                        if (bomRouting.getIsConsign().equals("Y") && !delBr.contains(bomRouting)) {
                            delBr.add(bomRouting);
                        }
                        break;
                    case "1":
                        if (bomRouting.getIsConsign().equals("N") && !delBr.contains(bomRouting)) {
                            delBr.add(bomRouting);
                        }
                        break;
                    default:
                }
            }
            bomRoutingList.addAll(bomRoutings);
            bomRoutingList.removeAll(delBr);
        }
        Page<BomRouting> pageList = new Page<>();
        pageList.setRecords(bomRoutingList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Override
    public void saveBomRoutings(Bill bill, List<BomRouting> bomRoutingList) {
        if (bomRoutingList != null && bomRoutingList.size() > 0) {
            for (BomRouting entity : bomRoutingList) {
                //外键设置
                entity.setBillId(bill.getId());
                bomRoutingMapper.insert(entity);
            }
        }
    }

    @Override
    public void delBomRoutings(Bill bill) {
        bomRoutingMapper.deleteByMainId(bill.getId());
    }

    @Override
    public List<BomRouting> selectByMainId(String mainId) {
        return bomRoutingMapper.selectByMainId(mainId);
    }

    @Override
    public List<BomRouting> selectByBomId(String bomId) {
        return bomRoutingMapper.selectList(new QueryWrapper<BomRouting>().eq("bom_id", bomId));
    }
}
