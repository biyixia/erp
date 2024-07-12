package org.jeecg.modules.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.hslf.blip.WMF;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.bill.entity.Bill;
import org.jeecg.modules.bill.entity.BillingDetail;
import org.jeecg.modules.bill.entity.BomRouting;
import org.jeecg.modules.bill.entity.Inventory;
import org.jeecg.modules.bill.mapper.BillMapper;
import org.jeecg.modules.bill.mapper.BillingDetailMapper;
import org.jeecg.modules.bill.mapper.BomRoutingMapper;
import org.jeecg.modules.bill.service.IBillingDetailService;
import org.jeecg.modules.initial.entity.*;
import org.jeecg.modules.initial.mapper.*;
import org.jeecg.modules.initial.service.IBomSubroutineService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BillingDetailServiceImpl extends ServiceImpl<BillingDetailMapper, BillingDetail> implements IBillingDetailService {

    @Autowired
    private BillingDetailMapper billingDetailMapper;
    @Autowired
    private BillMapper billMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public List<BillingDetail> selectByMainId(String mainId) {
        QueryWrapper<BillingDetail> billingDetailQw = new QueryWrapper<>();
        billingDetailQw.eq("bill_id", mainId);
        List<BillingDetail> billingDetails = billingDetailMapper.selectList(billingDetailQw);
        for (BillingDetail billingDetail : billingDetails) {
            QueryWrapper<Material> materialQw = new QueryWrapper<>();
            materialQw.eq("code", billingDetail.getMaterialCode());
            Material material = materialMapper.selectOne(materialQw);
            billingDetail.setMaterial(material);
        }
        return billingDetails;
    }

    public List<BillingDetail> selectByMainId(String mainId, String materialParam) {
        QueryWrapper<Material> materialQw = new QueryWrapper<>();

        if (oConvertUtils.isNotEmpty(materialParam)) {
            materialQw.eq("code", materialParam).or().eq("material_name", materialParam).or().eq("size", materialParam);
        }
        List<Material> materials = materialMapper.selectList(materialQw);
        ArrayList<String> ids = new ArrayList<>();
        for (Material material : materials) {
            ids.add(material.getCode());
        }
        QueryWrapper<BillingDetail> billingDetailQw = new QueryWrapper<>();
        billingDetailQw.eq("bill_id", mainId);
        if (ids.size() <= 0) {
            return new ArrayList<>();
        }
        billingDetailQw.in("material_code", ids);
        List<BillingDetail> billingDetails = billingDetailMapper.selectList(billingDetailQw);
        for (BillingDetail billingDetail : billingDetails) {
            materialQw = new QueryWrapper<>();
            materialQw.eq("code", billingDetail.getMaterialCode());
            Material material = materialMapper.selectOne(materialQw);
            billingDetail.setMaterial(material);
        }
        return billingDetails;
    }

    @Override
    public IPage<BillingDetail> queryPoPageList(boolean total, String type, String billCode, String clientId, String materialParam, String beginTime,
                                                String endTime, int pageSize, int pageNo, String inWarehouseId, String outWarehouseId) {
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
        if (oConvertUtils.isNotEmpty(inWarehouseId)) {
            queryWrapper.eq("in_warehouse_id", inWarehouseId);
        }
        if (oConvertUtils.isNotEmpty(outWarehouseId)) {
            queryWrapper.eq("out_warehouse_id", outWarehouseId);
        }
        queryWrapper.eq("bill_mode", type);

        if (total) {
            queryWrapper.notIn("status", new String[]{"0"});
        } else {
            queryWrapper.in("status", new String[]{"1", "2"});
        }

        if (type.equals("2")) {
            List<String> ids = new ArrayList<>();
            List<Warehouse> warehouses = warehouseMapper.selectList(new QueryWrapper<Warehouse>().eq("ownership", "供应商仓库"));
            if (warehouses.size() > 0) {
                for (Warehouse warehouse : warehouses) {
                    ids.add(warehouse.getId());
                }
                queryWrapper.notIn("in_warehouse_id", ids);
            }
        }
        if (type.equals("19")) {
            List<String> ids = new ArrayList<>();
            List<Warehouse> warehouses = warehouseMapper.selectList(new QueryWrapper<Warehouse>().eq("ownership", "供应商仓库"));
            if (warehouses.size() > 0) {
                for (Warehouse warehouse : warehouses) {
                    ids.add(warehouse.getId());
                }
                queryWrapper.in("out_warehouse_id", ids);
            }
        }
        //1.根据参数条件查询主表
        List<Bill> billList = billMapper.selectList(queryWrapper);
        //2.再根据主表的id查找所有的附表
        List<BillingDetail> billingDetailList = new ArrayList<>();
        for (Bill bill : billList) {
            bill.setMerchant(merchantMapper.selectById(bill.getClientId()));
            if (bill.getInWarehouseId() != null) {
                bill.setInWarehouse(warehouseMapper.selectById(bill.getInWarehouseId()));
            }
            if (bill.getOutWarehouseId() != null) {
                bill.setOutWarehouse(warehouseMapper.selectById(bill.getOutWarehouseId()));
            }
            List<BillingDetail> billingDetails = this.selectByMainId(bill.getId(), materialParam);
            for (BillingDetail billingDetail : billingDetails) {
                billingDetail.setBill(bill);
            }
            if (!total && (type.equals("3") || type.equals("7"))) {
                ArrayList<BillingDetail> bd1 = new ArrayList<>();
                for (BillingDetail billingDetail : billingDetails) {
                    BillingDetail b1 = billingDetailMapper.selectById(billingDetail.getReferId());
                    if (b1 != null) {
                        BillingDetail b2 = billingDetailMapper.selectById(b1.getReferId());
                        if (b2 != null) {
                            Bill b = billMapper.selectById(b2.getBillId());
                            if (b.getPaymentStyle().equals("按条款")) {
                                bd1.add(billingDetail);
                            }
                        }
                    }
                }
                billingDetails.removeAll(bd1);
            }
            ArrayList<BillingDetail> bd1 = new ArrayList<>();
            for (BillingDetail billingDetail : billingDetails) {
                if (!total && (billingDetail.getSumInBound() >= billingDetail.getCount() || billingDetail.getSumOutBound() >= billingDetail.getCount())) {
                    bd1.add(billingDetail);
                }
            }
            billingDetailList.removeAll(bd1);
            billingDetailList.addAll(billingDetails);
        }
        Page<BillingDetail> pageList = new Page<>();
        pageList.setRecords(billingDetailList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Override
    public void saveBillingDetails(Bill bill, List<BillingDetail> billingDetailList) {
        if (billingDetailList != null && billingDetailList.size() > 0) {
            List<String> bills = new ArrayList<>();
            List<String> bills1 = new ArrayList<>();
            List<String> bills2 = new ArrayList<>();
            for (BillingDetail entity : billingDetailList) {
                //外键设置
                entity.setBillId(bill.getId());
                //更新累计入库数量
                if (("2".equals(bill.getBillMode()) && "采购订单".equals(entity.getSource())) || ("3".equals(bill.getBillMode()) && "采购入库单".equals(entity.getSource()))
                        || ("1".equals(bill.getBillMode()) && "补料申请单".equals(entity.getSource()))
                ) {
                    //获取引用明细
                    BillingDetail bd = billingDetailMapper.selectOne(new QueryWrapper<BillingDetail>().eq("id", entity.getReferId()));
                    if (bd != null) {
                        //设置明细的累计入库数量/累计发票数量
                        bd.setSumInBound(bd.getSumInBound() + entity.getCount());
                        bills.add(bd.getBillId());
                        billingDetailMapper.updateById(bd);
                    }
                }
                //更新累计出库数量
                if (("6".equals(bill.getBillMode()) && "销售订单".equals(entity.getSource())) || ("7".equals(bill.getBillMode()) && "销售出库单".equals(entity.getSource()))
                        || ("9".equals(bill.getBillMode()) && "库存调拨单".equals(entity.getSource())) || ("15".equals(bill.getBillMode()) && "部门领料申请单".equals(entity.getSource()))
                        || ("19".equals(bill.getBillMode()) && "生产领料申请单".equals(entity.getSource())) || ("19".equals(bill.getBillMode()) && "生产订单".equals(entity.getSource()))
                ) {
                    //获取引用明细
                    BillingDetail bd = billingDetailMapper.selectOne(new QueryWrapper<BillingDetail>().eq("id", entity.getReferId()));
                    if (bd != null) {
                        //设置明细的累计出库数量
                        bd.setSumOutBound(bd.getSumOutBound() + entity.getCount());
                        bills1.add(bd.getBillId());
                        billingDetailMapper.updateById(bd);
                    }
                }
                if (("10".equals(bill.getBillMode()) && "库存调拨单".equals(entity.getSource()))
                ) {
                    //获取引用明细
                    BillingDetail bd = billingDetailMapper.selectOne(new QueryWrapper<BillingDetail>().eq("id", entity.getReferId()));
                    if (bd != null) {
                        //设置明细的累计入库数量/累计发票数量
                        bd.setSumInBound(bd.getSumInBound() + entity.getCount());
                        bills2.add(bd.getBillId());
                        billingDetailMapper.updateById(bd);
                    }
                }
                if (("20".equals(bill.getBillMode()) && "生产订单".equals(entity.getSource()))
                ) {
                    Bill b = billMapper.selectById(entity.getReferId());
                    if (b != null) {
                        //设置明细的累计入库数量/累计发票数量
                        b.setInvoiceNumber(String.valueOf(Double.parseDouble(b.getInvoiceNumber()) + entity.getCount()));
                        if (Double.parseDouble(b.getInvoiceNumber()) < b.getProduceNumber()) {
                            b.setStatus("4");
                        } else {
                            b.setStatus("5");
                        }
                        billMapper.updateById(b);
                    }
                }
                billingDetailMapper.insert(entity);
            }
            for (String s : bills) {
                Bill b = billMapper.selectById(s);
                for (BillingDetail billingDetail : this.selectByMainId(b.getId())) {
                    if (billingDetail.getSumInBound() < billingDetail.getCount()) {
                        b.setStatus("2");
                        break;
                    }
                    b.setStatus("3");
                }
                billMapper.updateById(b);
            }
            for (String s : bills1) {
                Bill b = billMapper.selectById(s);
                for (BillingDetail billingDetail : this.selectByMainId(b.getId())) {
                    if (billingDetail.getSumOutBound() < billingDetail.getCount()) {
                        b.setStatus("2");
                        break;
                    }
                    b.setStatus("3");
                }
                billMapper.updateById(b);
            }
            for (String s : bills2) {
                Bill b = billMapper.selectById(s);
                for (BillingDetail billingDetail : this.selectByMainId(b.getId())) {
                    if (billingDetail.getSumInBound() < billingDetail.getCount()) {
                        b.setStatus("4");
                        break;
                    }
                    b.setStatus("5");
                }
                billMapper.updateById(b);
            }
        }
    }

    @Override
    public void delBillingDetails(Bill bill) {
        List<String> bills = new ArrayList<>();
        List<String> bills1 = new ArrayList<>();
        List<String> bills2 = new ArrayList<>();
        //删除对应的累计入库数量
        if ("2".equals(bill.getBillMode()) || "3".equals(bill.getBillMode())
                || "1".equals(bill.getBillMode())
        ) {
            //获取所有的明细
            List<BillingDetail> all = selectByMainId(bill.getId());
            for (BillingDetail billingDetail : all) {
                //获取引用的明细对象并删除对应的累计入库数量/累计发票数量
                BillingDetail bd = billingDetailMapper.selectById(billingDetail.getReferId());
                if (bd != null) {
                    bills.add(bd.getBillId());
                    bd.setSumInBound(bd.getSumInBound() - billingDetail.getCount());
                    billingDetailMapper.updateById(bd);
                }
            }
        }
        //删除生产入库单
        if ("20".equals(bill.getBillMode())
        ) {
            //获取所有的明细
            List<BillingDetail> all = selectByMainId(bill.getId());
            for (BillingDetail billingDetail : all) {
                Bill b = billMapper.selectById(billingDetail.getReferId());
                b.setInvoiceNumber(String.valueOf(Double.parseDouble(b.getInvoiceNumber()) - billingDetail.getCount()));
                if (Double.parseDouble(b.getInvoiceNumber()) == 0) {
                    b.setStatus("3");
                } else if (Double.parseDouble(b.getInvoiceNumber()) < b.getProduceNumber()) {
                    b.setStatus("4");
                }
                billMapper.updateById(b);
            }
        }
        //删除调拨入库单
        if ("10".equals(bill.getBillMode())
        ) {
            //获取所有的明细
            List<BillingDetail> all = selectByMainId(bill.getId());
            for (BillingDetail billingDetail : all) {
                //获取引用的明细对象并删除对应的累计入库数量/累计发票数量
                BillingDetail bd = billingDetailMapper.selectById(billingDetail.getReferId());
                if (bd != null) {
                    bills2.add(bd.getBillId());
                    bd.setSumInBound(bd.getSumInBound() - billingDetail.getCount());
                    billingDetailMapper.updateById(bd);
                }
            }
        }
        //删除对应的累计出库数量
        if ("9".equals(bill.getBillMode()) || "6".equals(bill.getBillMode()) || "7".equals(bill.getBillMode())
                || "15".equals(bill.getBillMode()) || "19".equals(bill.getBillMode())
        ) {
            //获取所有的明细
            List<BillingDetail> all = selectByMainId(bill.getId());
            for (BillingDetail billingDetail : all) {
                //获取引用的明细对象并删除对应的累计入库数量/累计发票数量
                BillingDetail bd = billingDetailMapper.selectById(billingDetail.getReferId());
                if (bd != null) {
                    bills1.add(bd.getBillId());
                    bd.setSumOutBound(bd.getSumOutBound() - billingDetail.getCount());
                    billingDetailMapper.updateById(bd);
                }
            }
        }
        billingDetailMapper.deleteByMainId(bill.getId());
        for (String s : bills) {
            Bill b = billMapper.selectById(s);
            for (BillingDetail billingDetail : this.selectByMainId(b.getId())) {
                if (billingDetail.getSumInBound() == 0) {
                    b.setStatus("1");
                } else if (billingDetail.getSumInBound() < billingDetail.getCount()) {
                    b.setStatus("2");
                    break;
                }
            }
            billMapper.updateById(b);
        }
        for (String s : bills1) {
            Bill b = billMapper.selectById(s);
            for (BillingDetail billingDetail : this.selectByMainId(b.getId())) {
                if (billingDetail.getSumOutBound() == 0) {
                    b.setStatus("1");
                } else if (billingDetail.getSumOutBound() < billingDetail.getCount()) {
                    b.setStatus("2");
                    break;
                }
            }
            billMapper.updateById(b);
        }
        for (String s : bills2) {
            Bill b = billMapper.selectById(s);
            for (BillingDetail billingDetail : this.selectByMainId(b.getId())) {
                if (billingDetail.getSumInBound() == 0) {
                    b.setStatus("3");
                } else if (billingDetail.getSumInBound() < billingDetail.getCount()) {
                    b.setStatus("4");
                    break;
                }
            }
            billMapper.updateById(b);
        }
    }

    @Override
    public IPage<BillingDetail> queryROPList(String materialCode, int pageNo, int pageSize) {
        Material material = materialMapper.selectOne(new QueryWrapper<Material>().eq("code", materialCode));
        List<BillingDetail> netRequire = null;
        if (material.getPlanType().equals("ROP")) {
            netRequire = this.getNetRequire(materialCode);
        }
        Page<BillingDetail> pageList = new Page<>();
        pageList.setRecords(netRequire);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Override
    public IPage<BillingDetail> queryROP(String materialParam, int pageNo, int pageSize, String inWarehouseId, String outWarehouseId) {
        List<BillingDetail> billingDetailList = new ArrayList<>();
        for (Material material : materialMapper.selectList(null)) {
            if ("ROP".equals(material.getPlanType())) {
                //再订货点
                double rop = material.getSafeStock() + material.getLeadDays() * material.getMaximumDailySupply();
                double enable = 0.0;
                List<BillingDetail> records = this.queryROPList(material.getCode(), pageNo, pageSize).getRecords();
                if (records.size() > 0) {
                    enable = records.get(records.size() - 1).getNumber();
                }
                if (enable < rop) {
                    BillingDetail billingDetail = new BillingDetail();
                    billingDetail.setMaterialCode(material.getCode());
                    billingDetail.setMaterial(materialMapper.selectOne(new QueryWrapper<Material>().eq("code", material.getCode())));
                    billingDetail.setCount(material.getMaximumStock() - enable);
                    billingDetailList.add(billingDetail);
                }
            }
        }
        Page<BillingDetail> pageList = new Page<>();
        pageList.setRecords(billingDetailList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Autowired
    BomMapper bomMapper;
    @Autowired
    IBomSubroutineService bomSubroutineService;

    List<BillingDetail> getNetRequire(String materialCode) {
        List<BillingDetail> billingDetailList = new ArrayList<>();
        Material material = materialMapper.selectOne(new QueryWrapper<Material>().eq("code", materialCode));
        Double balance = 0.0;
        //现有库存量生成供应数量
        for (Inventory inventory : billingDetailMapper.materialInventory(materialCode)) {
            BillingDetail billingDetail = new BillingDetail();
            billingDetail.setMaterialCode(inventory.getMaterialCode());
            billingDetail.setMaterial(material);
            billingDetail.setSumInBound(Double.parseDouble(inventory.getSumCount()));
            balance += Double.parseDouble(inventory.getSumCount());
            billingDetail.setNumber(balance);
            billingDetailList.add(billingDetail);
        }
        //采购订单、补料申请生成供应数量
        QueryWrapper<Bill> qw = new QueryWrapper<>();
        qw.in("bill_mode", new String[]{"1", "13"});
        qw.in("status", new String[]{"1", "2"});
        //1.根据参数条件查询主表
        List<Bill> list2 = billMapper.selectList(qw);
        //2.再根据主表的id查找所有的附表
        for (Bill bill : list2) {
            List<BillingDetail> billingDetails = this.selectByMainId(bill.getId(), materialCode);
            ArrayList<BillingDetail> delBillingDetails = new ArrayList<>();
            for (BillingDetail billingDetail : billingDetails) {
                if (billingDetail.getSumInBound() >= billingDetail.getCount()) {
                    delBillingDetails.add(billingDetail);
                    continue;
                }
                if (bill.getBillMode().equals("1")) {
//                    bill.setDemandedDate(bill.getEstimatedDeliveryDate());
                    billingDetail.setDemandedDate(billingDetail.getExpectedDelivery());
                }
                billingDetail.setBill(bill);
                billingDetail.setSumInBound(billingDetail.getCount() - billingDetail.getSumInBound());
                balance += billingDetail.getSumInBound();
                billingDetail.setNumber(balance);
            }
            billingDetails.removeAll(delBillingDetails);
            billingDetailList.addAll(billingDetails);
        }
        //生产订单未入库生成供应数量
        QueryWrapper<Bill> qw1 = new QueryWrapper<>();
        qw1.in("bill_mode", new String[]{"17"});
        qw1.in("status", new String[]{"1", "2", "3", "4"});
        Material m = materialMapper.selectOne(new QueryWrapper<Material>().eq("code", materialCode));
        qw1.in("product_id", m.getId());
        qw1.eq("del_flag", "0");
        //1.根据参数条件查询主表
        List<Bill> list3 = billMapper.selectList(qw1);
        //2.再根据主表的id查找所有的附表
        for (Bill bill : list3) {
            if (Double.parseDouble(bill.getInvoiceNumber()) >= bill.getProduceNumber()) {
                break;
            }
            BillingDetail billingDetail = new BillingDetail();
            billingDetail.setBill(bill);
            billingDetail.setDemandedDate(bill.getDemandedDate());
            billingDetail.setMaterial(m);
            billingDetail.setSumInBound(bill.getProduceNumber() - Double.parseDouble(bill.getInvoiceNumber()));
            balance += billingDetail.getSumInBound();
            billingDetail.setNumber(balance);
            billingDetailList.add(billingDetail);
        }
        //销售订单、部门领料申请、生产领料申请生成需求数量
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("bill_mode", new String[]{"5", "14", "18"});
        queryWrapper.in("status", new String[]{"1", "2"});
        //1.根据参数条件查询主表
        List<Bill> list1 = billMapper.selectList(queryWrapper);
        for (Bill bill : list1) {
            List<BillingDetail> billingDetails = this.selectByMainId(bill.getId(), materialCode);
            ArrayList<BillingDetail> delBillingDetails = new ArrayList<>();
            for (BillingDetail billingDetail : billingDetails) {
                if (billingDetail.getSumOutBound() >= billingDetail.getCount()) {
                    delBillingDetails.add(billingDetail);
                    continue;
                }
                if (bill.getBillMode().equals("5")) {
//                    bill.setDemandedDate(bill.getEstimatedDeliveryDate());
                    billingDetail.setDemandedDate(billingDetail.getExpectedDelivery());
                }
                billingDetail.setBill(bill);
                billingDetail.setSumOutBound(billingDetail.getCount() - billingDetail.getSumOutBound());
                balance -= billingDetail.getSumOutBound();
                billingDetail.setNumber(balance);
            }
            billingDetails.removeAll(delBillingDetails);
            billingDetailList.addAll(billingDetails);
        }
        return billingDetailList;
    }

    @Override
    public IPage<BillingDetail> queryMPSList(String materialCode, int pageNo, int pageSize) {
        List<BillingDetail> billingDetailList = new ArrayList<>();
        Material material = materialMapper.selectOne(new QueryWrapper<Material>().eq("code", materialCode));
        if (material.getPlanType().equals("MPS")) {
            billingDetailList = this.getNetRequire(materialCode);
        }
        Page<BillingDetail> pageList = new Page<>();
        pageList.setRecords(billingDetailList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Override
    public IPage<Bill> queryMPS(int pageNo, int pageSize) {
        List<Bill> billList = new ArrayList<>();
        for (Material material : materialMapper.selectList(null)) {
            if ("MPS".equals(material.getPlanType())) {
                double enable = 0.0;
                List<BillingDetail> records = this.queryMPSList(material.getCode(), pageNo, pageSize).getRecords();
                if (records.size() > 0) {
                    enable = records.get(records.size() - 1).getNumber();
                }
                if (enable < 0) {
                    Bill bill = new Bill();
                    bill.setProduct(materialMapper.selectOne(new QueryWrapper<Material>().eq("code", material.getCode())));
                    bill.setProductId(material.getId());
                    bill.setProduceNumber(-enable);
                    for (BillingDetail record : records) {
                        if (record.getNumber() < 0 && record.getBill() != null) {
                            bill.setDemandedDate(record.getDemandedDate());
                            break;
                        }
                    }
                    billList.add(bill);
                }
            }
        }
        Page<Bill> pageList = new Page<>();
        pageList.setRecords(billList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Override
    public IPage<BillingDetail> queryMRP(int pageNo, int pageSize) {
        ArrayList<BillingDetail> billingDetailArrayList = new ArrayList<>();
        IPage<Bill> billIPage = this.queryMPS(pageNo, pageSize);
        for (Bill record : billIPage.getRecords()) {
            ArrayList<BillingDetail> del = new ArrayList<>();
            //先获取MPS结果中的产品
            //获取每个产品对应的BOM
            Bom bom = bomMapper.selectOne(new QueryWrapper<Bom>().eq("prod_id", record.getProductId()).eq("is_default", "Y"));
            ArrayList<BillingDetail> billingDetails = new ArrayList<>();
            for (BomSubroutine bomSubroutine : bomSubroutineService.selectByMainId(bom.getId())) {
                bomSubroutineService.selectAllById(billingDetails, bomSubroutine, record.getProduceNumber(), record.getDemandedDate());
            }
            //计算出所有配件的数量
            for (BillingDetail billingDetail : billingDetails) {
                List<BillingDetail> netRequire = this.getNetRequire(billingDetail.getMaterialCode());
                Double number = 0.0;
                if (netRequire.size() > 0) {
                    number = netRequire.get(netRequire.size() - 1).getNumber();
                }
                billingDetail.setCount(billingDetail.getCount() - number);
                billingDetail.setMaterial(materialMapper.selectOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode())));
                if (billingDetail.getCount() - number <= 0) {
                    del.add(billingDetail);
                }
            }
            billingDetailArrayList.addAll(billingDetails);
            billingDetailArrayList.removeAll(del);
        }
        Page<BillingDetail> pageList = new Page<>();
        Collections.sort(billingDetailArrayList);
        pageList.setRecords(billingDetailArrayList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Override
    public IPage<BillingDetail> queryPurchaseMRP(int pageNo, int pageSize) {
        ArrayList<BillingDetail> billingDetailArrayList = new ArrayList<>();
        IPage<Bill> billIPage = this.queryMPS(pageNo, pageSize);
        for (Bill record : billIPage.getRecords()) {
            ArrayList<BillingDetail> del = new ArrayList<>();
            //先获取MPS结果中的产品
            //获取每个产品对应的BOM
            Bom bom = bomMapper.selectOne(new QueryWrapper<Bom>().eq("prod_id", record.getProductId()).eq("is_default", "Y"));
            ArrayList<BillingDetail> billingDetails = new ArrayList<>();
            for (BomSubroutine bomSubroutine : bomSubroutineService.selectByMainId(bom.getId())) {
                bomSubroutineService.selectAllById(billingDetails, bomSubroutine, record.getProduceNumber(), record.getDemandedDate());
            }
            //计算出所有配件的数量
            for (BillingDetail billingDetail : billingDetails) {
                List<BillingDetail> netRequire = this.getNetRequire(billingDetail.getMaterialCode());
                Double number = 0.0;
                if (netRequire.size() > 0) {
                    number = netRequire.get(netRequire.size() - 1).getNumber();
                }
                billingDetail.setCount(billingDetail.getCount() - number);
                billingDetail.setMaterial(materialMapper.selectOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode())));
                if (billingDetail.getCount() - number <= 0) {
                    del.add(billingDetail);
                }
                if (billingDetail.getIsOutput().equals("Y")) {
                    del.add(billingDetail);
                }
            }
            billingDetailArrayList.addAll(billingDetails);
            billingDetailArrayList.removeAll(del);
        }
        Page<BillingDetail> pageList = new Page<>();
        Collections.sort(billingDetailArrayList);
        pageList.setRecords(billingDetailArrayList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }

    @Override
    public IPage<BillingDetail> queryInventory(String materialParam, int pageNo, int pageSize, String inWarehouseId, String outWarehouseId) {
        List<BillingDetail> billingDetailList = new ArrayList<>();
        int i = 0;
        for (Inventory inventory : billingDetailMapper.queryInventory()) {
            BillingDetail bd = new BillingDetail();
            Bill bill = new Bill();
            bill.setInWarehouseId(inventory.getInWarehouseId());
            bill.setInWarehouse(warehouseMapper.selectById(inventory.getInWarehouseId()));
            bd.setBill(bill);
            bd.setId(String.valueOf(i++));
            bd.setCount(Double.parseDouble(inventory.getSumCount()));
            bd.setMaterialCode(inventory.getMaterialCode());
            bd.setMaterial(materialMapper.selectOne(new QueryWrapper<Material>().eq("code", inventory.getMaterialCode())));
            billingDetailList.add(bd);
        }
        Page<BillingDetail> page = new Page<>(pageNo, pageSize);
        page.setRecords(billingDetailList);
        return page;
    }

    @Override
    public int inventoryByWarehouseAndCode(String warehouseId, String code) {
        if (billingDetailMapper.inventoryByWarehouseAndCode(warehouseId, code) != null) {
            return billingDetailMapper.inventoryByWarehouseAndCode(warehouseId, code);
        } else {
            return 0;
        }
    }
}



