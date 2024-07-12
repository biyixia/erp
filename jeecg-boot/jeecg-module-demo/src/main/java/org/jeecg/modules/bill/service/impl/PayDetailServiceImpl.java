package org.jeecg.modules.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.bill.entity.*;
import org.jeecg.modules.bill.mapper.*;
import org.jeecg.modules.bill.service.IBillingDetailService;
import org.jeecg.modules.bill.service.IPayDetailService;
import org.jeecg.modules.initial.mapper.MerchantMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 付款明细
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@Service
public class PayDetailServiceImpl extends ServiceImpl<PayDetailMapper, PayDetail> implements IPayDetailService {
	@Autowired
	private IBillingDetailService billingDetailService;
	@Autowired
	private PayDetailMapper payDetailMapper;
	@Autowired
	private BillingDetailMapper billingDetailMapper;
	@Autowired
	private BillMapper billMapper;
	@Autowired
	private MerchantMapper merchantMapper;
	@Autowired
	private CollectPayPlanMapper collectPayPlanMapper;
	@Autowired
	private CraftDetailMapper craftDetailMapper;
	@Override
	public void savePayDetails(Bill bill, List<PayDetail> payDetailList) {
		if (payDetailList != null && payDetailList.size() > 0) {
			List<String> bills1 = new ArrayList<>();
			List<String> bills2 = new ArrayList<>();
			List<String> bills3 = new ArrayList<>();
			List<String> bills4 = new ArrayList<>();
			for (PayDetail entity : payDetailList) {
				//外键设置
				entity.setByxBill(bill.getId());
				if ("4".equals(bill.getBillMode()) && "采购发票".equals(entity.getSource())) {
					//获取引用明细
					BillingDetail bd = billingDetailMapper.selectOne(new QueryWrapper<BillingDetail>().eq("id", entity.getReferId()));
					//设置明细的累计入库数量/累计发票数量
					bd.setSumInBound(bd.getSumInBound() + entity.getApplyAmount());
					bills1.add(bd.getBillId());
					billingDetailMapper.updateById(bd);
				}
				if ("4".equals(bill.getBillMode()) && "采购订单".equals(entity.getSource())) {
					//获取引用明细
					CollectPayPlan cpp = collectPayPlanMapper.selectOne(new QueryWrapper<CollectPayPlan>().eq("id", entity.getReferId()));
					//设置明细的累计入库数量/累计发票数量
					cpp.setSumReqAmount(cpp.getSumReqAmount() + entity.getApplyAmount());
					collectPayPlanMapper.updateById(cpp);
				}
				if ("25".equals(bill.getBillMode()) && "生产委托结算单".equals(entity.getSource())) {
					//获取引用明细
					CraftDetail cd = craftDetailMapper.selectById(entity.getReferId());
					if (cd != null) {
						bills4.add(cd.getBillId());
						cd.setProcessInQuantity(cd.getProcessInQuantity() + entity.getApplyAmount());
						craftDetailMapper.updateById(cd);
					}
				}
				if ("26".equals(bill.getBillMode()) && "采购付款申请".equals(entity.getSource())) {
					//获取引用明细
					PayDetail pd = payDetailMapper.selectOne(new QueryWrapper<PayDetail>().eq("id", entity.getReferId()));
					if (pd != null) {
						bills2.add(pd.getByxBill());
						pd.setSumApplyAmount(pd.getSumApplyAmount() + entity.getApplyAmount());
						payDetailMapper.updateById(pd);
					}
				}
				if ("27".equals(bill.getBillMode()) && "销售发票".equals(entity.getSource())) {
					//获取引用明细
					BillingDetail bd = billingDetailMapper.selectById(entity.getReferId());
					if (bd != null) {
						bills3.add(bd.getBillId());
						bd.setSumOutBound(bd.getSumOutBound() + entity.getApplyAmount());
						billingDetailMapper.updateById(bd);
					}
				}
				if ("27".equals(bill.getBillMode()) && "销售订单".equals(entity.getSource())) {
					//获取引用明细
					CollectPayPlan cpp = collectPayPlanMapper.selectOne(new QueryWrapper<CollectPayPlan>().eq("id", entity.getReferId()));
					cpp.setSumReqAmount(cpp.getSumReqAmount() + entity.getApplyAmount());
					collectPayPlanMapper.updateById(cpp);
				}
				payDetailMapper.insert(entity);
			}
			for (String s : bills1) {
				Bill b = billMapper.selectById(s);
				for (BillingDetail billingDetail : billingDetailService.selectByMainId(b.getId())) {
					if (billingDetail.getSumInBound() < billingDetail.getTaxIncludedAmount()) {
						b.setStatus("2");
						break;
					}
					b.setStatus("3");
				}
				billMapper.updateById(b);
			}
			for (String s : bills2) {
				Bill b = billMapper.selectById(s);
				for (PayDetail payDetail : payDetailMapper.selectByMainId(b.getId())) {
					if (payDetail.getSumApplyAmount() < payDetail.getApplyAmount()) {
						b.setStatus("2");
						break;
					}
					b.setStatus("3");
				}
				billMapper.updateById(b);
			}
			for (String s : bills3) {
				Bill b = billMapper.selectById(s);
				for (BillingDetail billingDetail : billingDetailService.selectByMainId(b.getId())) {
					if (billingDetail.getSumOutBound() < billingDetail.getTaxIncludedAmount()) {
						b.setStatus("2");
						break;
					}
					b.setStatus("3");
				}
				billMapper.updateById(b);
			}
			for (String s : bills4) {
				Bill b = billMapper.selectById(s);
				for (CraftDetail craftDetail : craftDetailMapper.selectByMainId(b.getId())) {
					if (craftDetail.getProcessInQuantity() < craftDetail.getTotalAmount()) {
						b.setStatus("2");
						break;
					}
					b.setStatus("3");
				}
				billMapper.updateById(b);
			}
		}
	}

	@Override
	public void delPayDetails(Bill bill) {
		if ("4".equals(bill.getBillMode())) {
			//获取所有的明细
			List<PayDetail> all = payDetailMapper.selectByMainId(bill.getId());
			for (PayDetail payDetail : all) {
				//获取引用的明细对象并删除对应的累计入库数量/累计发票数量
				BillingDetail bd = billingDetailMapper.selectById(payDetail.getReferId());
				if (bd != null) {
					bd.setSumInBound(bd.getSumInBound() - payDetail.getApplyAmount());
					billingDetailMapper.updateById(bd);
				}
				CollectPayPlan cpp = collectPayPlanMapper.selectById(payDetail.getReferId());
				if (cpp != null) {
					cpp.setSumReqAmount(cpp.getSumReqAmount() - payDetail.getApplyAmount());
					collectPayPlanMapper.updateById(cpp);
				}
			}
		}
		if ("25".equals(bill.getBillMode())) {
			//获取所有的明细
			List<PayDetail> all = payDetailMapper.selectByMainId(bill.getId());
			for (PayDetail payDetail : all) {
				CraftDetail cd = craftDetailMapper.selectById(payDetail.getReferId());
				if (cd != null) {
					cd.setProcessInQuantity(cd.getProcessInQuantity() - payDetail.getApplyAmount());
					craftDetailMapper.updateById(cd);
				}
			}
		}
		if ("26".equals(bill.getBillMode())) {
			//获取所有的明细
			List<PayDetail> all = payDetailMapper.selectByMainId(bill.getId());
			for (PayDetail payDetail : all) {
				PayDetail bd = payDetailMapper.selectById(payDetail.getReferId());
				if (bd != null) {
					bd.setSumApplyAmount(bd.getSumApplyAmount() - payDetail.getApplyAmount());
					payDetailMapper.updateById(bd);
				}
			}
		}
		if ("27".equals(bill.getBillMode())) {
			List<PayDetail> all = payDetailMapper.selectByMainId(bill.getId());
			for (PayDetail payDetail : all) {
				//获取引用明细
				BillingDetail bd = billingDetailMapper.selectById(payDetail.getReferId());
				if (bd != null) {
					bd.setSumOutBound(bd.getSumOutBound() - payDetail.getApplyAmount());
					billingDetailMapper.updateById(bd);
				}
				CollectPayPlan cpp = collectPayPlanMapper.selectById(payDetail.getReferId());
				if (cpp != null) {
					cpp.setSumReqAmount(cpp.getSumReqAmount() - payDetail.getApplyAmount());
					collectPayPlanMapper.updateById(cpp);
				}
			}
		}
		payDetailMapper.deleteByMainId(bill.getId());
	}

	@Override
	public List<PayDetail> selectByMainId(String mainId) {
		return payDetailMapper.selectByMainId(mainId);
	}

	@Override
	public IPage<PayDetail> queryPayDetailList(boolean total,String type,String billCode, String clientId, String materialParam, String beginTime, String endTime, int pageNo, int pageSize) {
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
		if (total) {
			queryWrapper.in("status", new String[]{"1", "2", "3"});
		} else {
			queryWrapper.in("status", new String[]{"1", "2"});
		}

		//1.根据参数条件查询主表
		List<Bill> billList = billMapper.selectList(queryWrapper);
		//2.再根据主表的id查找所有的附表
		List<PayDetail> payDetailList = new ArrayList<>();
		for (Bill bill : billList) {
			List<PayDetail> payDetails = this.selectByMainId(bill.getId());
			for (PayDetail payDetail : payDetails) {
				payDetail.setMerchant(merchantMapper.selectById(payDetail.getClientId()));
				payDetail.setBill(bill);
			}
			ArrayList<PayDetail> pds = new ArrayList<>();
			for (PayDetail payDetail : payDetails) {
				if (!total && (payDetail.getSumApplyAmount() >= payDetail.getApplyAmount())) {
					pds.add(payDetail);
				}
			}
			payDetails.removeAll(pds);
			payDetailList.addAll(payDetails);
		}
		Page<PayDetail> pageList = new Page<>();
		pageList.setRecords(payDetailList);
		pageList.setCurrent(pageNo);
		pageList.setTotal(pageSize);
		return pageList;
	}
}
