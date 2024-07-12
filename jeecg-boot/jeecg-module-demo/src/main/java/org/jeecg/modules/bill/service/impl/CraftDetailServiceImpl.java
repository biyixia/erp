package org.jeecg.modules.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.bill.entity.*;
import org.jeecg.modules.bill.mapper.BillMapper;
import org.jeecg.modules.bill.mapper.BomRoutingMapper;
import org.jeecg.modules.bill.mapper.CraftDetailMapper;
import org.jeecg.modules.bill.service.ICraftDetailService;
import org.jeecg.modules.initial.entity.Craft;
import org.jeecg.modules.initial.entity.Material;
import org.jeecg.modules.initial.mapper.CraftMapper;
import org.jeecg.modules.initial.mapper.MaterialMapper;
import org.jeecg.modules.initial.mapper.MerchantMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 工艺明细
 * @Author: jeecg-boot
 * @Date: 2023-05-04
 * @Version: V1.0
 */
@Service
public class CraftDetailServiceImpl extends ServiceImpl<CraftDetailMapper, CraftDetail> implements ICraftDetailService {
    @Autowired
    private CraftDetailMapper craftDetailMapper;
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private BomRoutingMapper bomRoutingMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public void saveCraftDetails(Bill bill, List<CraftDetail> craftDetailList) {
        if (craftDetailList != null && craftDetailList.size() > 0) {
            List<String> statusList = new ArrayList<>();
            for (CraftDetail entity : craftDetailList) {
                //外键设置
                entity.setBillId(bill.getId());
                if (("21".equals(bill.getBillMode()) && "生产订单工序".equals(entity.getSource()))
                ) {
                    //获取引用明细
                    BomRouting br = bomRoutingMapper.selectOne(new QueryWrapper<BomRouting>().eq("id", entity.getBomRoutingId()));
                    if (br != null) {
                        //设置明细的累计入库数量/累计发票数量
                        br.setCompleteCount(br.getCompleteCount() + entity.getCompleteQuantity());
                        bomRoutingMapper.updateById(br);
                    }
                }
                if (("22".equals(bill.getBillMode()) && "生产订单工序".equals(entity.getSource()))
                ) {
                    //获取引用明细
                    BomRouting br = bomRoutingMapper.selectOne(new QueryWrapper<BomRouting>().eq("id", entity.getBomRoutingId()));
                    if (br != null) {
                        //设置明细的累计入库数量/累计发票数量
                        br.setConsignCount(br.getConsignCount() + entity.getConsignQuantity());
                        bomRoutingMapper.updateById(br);
                    }
                }
                if (("23".equals(bill.getBillMode()) && "生产委托出库单".equals(entity.getSource())) || ("24".equals(bill.getBillMode()) && "生产委托入库单".equals(entity.getSource()))
                ) {
                    //获取引用明细
                    CraftDetail cd = craftDetailMapper.selectOne(new QueryWrapper<CraftDetail>().eq("id", entity.getReferId()));
                    if (cd != null) {
                        //设置明细的累计入库数量/累计发票数量
                        cd.setProcessInQuantity(cd.getProcessInQuantity() + entity.getCompleteQuantity());
                        statusList.add(cd.getBillId());
                        if (entity.getWasteQuantity() != null) {
                            cd.setWasteInQuantity(cd.getWasteInQuantity() + entity.getWasteQuantity());
                        }
                        craftDetailMapper.updateById(cd);
                    }
                }
                craftDetailMapper.insert(entity);
            }
            for (String s : statusList) {
                Bill b = billMapper.selectById(s);
                for (CraftDetail craftDetail : this.selectByMainId(b.getId())) {
                    if (craftDetail.getConsignQuantity() != null) {
                        if (craftDetail.getProcessInQuantity() < craftDetail.getConsignQuantity()) {
                            b.setStatus("2");
                            break;
                        }
                    } else if (craftDetail.getCompleteQuantity() != null) {
                        if (craftDetail.getProcessInQuantity() < craftDetail.getCompleteQuantity()) {
                            b.setStatus("2");
                            break;
                        }
                    }
                    b.setStatus("3");
                }
                billMapper.updateById(b);
            }
        }
    }

    @Override
    public void delCraftDetails(Bill bill) {
        //删除对应的累计入库数量
        if ("21".equals(bill.getBillMode())
        ) {
            //获取所有的明细
            List<CraftDetail> all = this.selectByMainId(bill.getId());
            for (CraftDetail craftDetail : all) {
                //获取引用的明细对象并删除对应的累计入库数量/累计发票数量
                BomRouting br = bomRoutingMapper.selectById(craftDetail.getBomRoutingId());
                if (br != null) {
                    br.setCompleteCount(br.getCompleteCount() - craftDetail.getCompleteQuantity());
                    bomRoutingMapper.updateById(br);
                }
            }
        }
        if ("22".equals(bill.getBillMode())
        ) {
            //获取所有的明细
            List<CraftDetail> all = this.selectByMainId(bill.getId());
            for (CraftDetail craftDetail : all) {
                //获取引用的明细对象并删除对应的累计入库数量/累计发票数量
                BomRouting br = bomRoutingMapper.selectById(craftDetail.getBomRoutingId());
                if (br != null) {
                    br.setConsignCount(br.getConsignCount() - craftDetail.getConsignQuantity());
                    bomRoutingMapper.updateById(br);
                }
            }
        }
        if ("23".equals(bill.getBillMode()) || "24".equals(bill.getBillMode())
        ) {
            //获取所有的明细
            List<CraftDetail> all = this.selectByMainId(bill.getId());
            for (CraftDetail craftDetail : all) {
                //获取引用的明细对象并删除对应的累计入库数量/累计发票数量
                CraftDetail cd = craftDetailMapper.selectById(craftDetail.getReferId());
                if (cd != null) {
                    cd.setProcessInQuantity(cd.getProcessInQuantity() - craftDetail.getCompleteQuantity());
                    cd.setWasteInQuantity(cd.getWasteInQuantity() - craftDetail.getWasteQuantity());
                    craftDetailMapper.updateById(cd);
                }
            }
        }
        craftDetailMapper.deleteByMainId(bill.getId());
    }


    @Override
    public List<CraftDetail> selectByMainId(String mainId) {
        return craftDetailMapper.selectByMainId(mainId);
    }

    @Override
    public IPage<CraftDetail> queryCraftDetailList(String type, String billCode, String clientId, String productId, String beginTime, String endTime, int pageNo, int pageSize) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        if (oConvertUtils.isNotEmpty(billCode)) {
            queryWrapper.eq("code", billCode);
        }
        if (oConvertUtils.isNotEmpty(productId)) {
            queryWrapper.eq("product_id", productId);
        }
        if (oConvertUtils.isNotEmpty(clientId)) {
            queryWrapper.eq("client_id", clientId);
        }
        if (oConvertUtils.isNotEmpty(beginTime) && oConvertUtils.isNotEmpty(endTime)) {
            queryWrapper.between("bill_date", beginTime, endTime);
        }
        queryWrapper.eq("bill_mode", type);
        queryWrapper.in("status", new String[]{"1", "2"});

        //1.根据参数条件查询主表
        List<Bill> billList = billMapper.selectList(queryWrapper);
        //2.再根据主表的id查找所有的附表
        List<CraftDetail> craftDetailList = new ArrayList<>();
        for (Bill bill : billList) {
            if (bill.getClientId() != null) {
                bill.setMerchant(merchantMapper.selectById(bill.getClientId()));
            }
            List<CraftDetail> craftDetails = this.selectByMainId(bill.getId());
            for (CraftDetail craftDetail : craftDetails) {
                craftDetail.setProduct(materialMapper.selectOne(new QueryWrapper<Material>().eq("code", craftDetail.getProductCode())));
                craftDetail.setBomRouting(bomRoutingMapper.selectById(craftDetail.getBomRoutingId()));
                craftDetail.setBill(bill);
            }
            craftDetailList.addAll(craftDetails);
        }
        Page<CraftDetail> pageList = new Page<>();
        pageList.setRecords(craftDetailList);
        pageList.setCurrent(pageNo);
        pageList.setTotal(pageSize);
        return pageList;
    }
}
