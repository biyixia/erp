package org.jeecg.modules.initial.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.bill.entity.BillingDetail;
import org.jeecg.modules.initial.entity.Bom;
import org.jeecg.modules.initial.entity.BomSubroutine;
import org.jeecg.modules.initial.entity.Material;
import org.jeecg.modules.initial.mapper.BomMapper;
import org.jeecg.modules.initial.mapper.BomSubroutineMapper;
import org.jeecg.modules.initial.mapper.MaterialMapper;
import org.jeecg.modules.initial.service.IBomSubroutineService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 子件明细
 * @Author: jeecg-boot
 * @Date: 2023-05-01
 * @Version: V1.0
 */
@Service
public class BomSubroutineServiceImpl extends ServiceImpl<BomSubroutineMapper, BomSubroutine> implements IBomSubroutineService {

    @Autowired
    private BomSubroutineMapper bomSubroutineMapper;

    @Autowired
    private BomMapper bomMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Override
    public List<BomSubroutine> selectByMainId(String mainId) {
        return bomSubroutineMapper.selectByMainId(mainId);
    }

    @Override
    public void selectAllById(List<BillingDetail> billingDetails, BomSubroutine bomSubroutine, Double index, Date demandedDate) {
        index = index * bomSubroutine.getNumerator()/bomSubroutine.getDenominator();
        boolean add = true;
        Date date = null;
        for (BillingDetail bd : billingDetails) {
            if (bd.getMaterialCode().equals(bomSubroutine.getCode())) {
                add = false;
                bd.setCount(bd.getCount() + index);
                break;
            }
        }
        //没有不同层次的相同物料
        if (add) {
            BillingDetail billingDetail = new BillingDetail();
            billingDetail.setMaterialCode(bomSubroutine.getCode());
            billingDetail.setCount(index);
            billingDetail.setIsOutput(bomSubroutine.getOutputLevel());

            Calendar calendar = Calendar.getInstance();//new一个Calendar类,把Date放进去
            if (demandedDate != null) {
                calendar.setTime(demandedDate);
            }
            calendar.add(Calendar.DATE,-bomSubroutine.getAdvenceDays().intValue());
            billingDetail.setDemandedDate(calendar.getTime());
            date = billingDetail.getDemandedDate();
            billingDetails.add(billingDetail);
        }
        if (bomSubroutine.getOutputLevel().equals("Y")) {
            Material material = materialMapper.selectOne(new QueryWrapper<Material>().eq("code", bomSubroutine.getCode()));
            Bom bom = bomMapper.selectOne(new QueryWrapper<Bom>().eq("prod_id", material.getId()).eq("is_default", "Y"));
            for (BomSubroutine subroutine : bomSubroutineMapper.selectByMainId(bom.getId())) {
                this.selectAllById(billingDetails, subroutine,index,date);
            }
        }
    }
}
