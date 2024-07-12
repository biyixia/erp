package org.jeecg.modules.bill.controller;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.modules.bill.entity.*;
import org.jeecg.modules.bill.service.*;
import org.jeecg.modules.initial.entity.Bom;
import org.jeecg.modules.initial.entity.Craft;
import org.jeecg.modules.initial.entity.Material;
import org.jeecg.modules.initial.entity.Merchant;
import org.jeecg.modules.initial.service.*;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.bill.vo.BillPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

@Api(tags = "单据管理")
@RestController
@RequestMapping("/bill/bill")
@Slf4j
public class BillController {
    @Autowired
    private IBillService billService;
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
    private IMerchantService merchantService;
    @Autowired
    private IWarehouseService warehouseService;
    @Autowired
    private IMaterialService materialService;

    /**
     * 分页列表查询
     *
     * @param bill
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "单据管理-分页列表查询")
    @ApiOperation(value = "单据管理-分页列表查询", notes = "单据管理-分页列表查询")
    @GetMapping(value = "/list/{typeCode}")
    public Result<IPage<Bill>> queryPageList(@PathVariable("typeCode") String typeCode, Bill bill,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             HttpServletRequest req) {
        QueryWrapper<Bill> queryWrapper = QueryGenerator.initQueryWrapper(bill, req.getParameterMap());
        switch (typeCode) {
            case "po":
                queryWrapper.eq("bill_mode", "1");
                break;
            case "poo":
                queryWrapper.eq("bill_mode", "2");
                break;
            case "pi":
                queryWrapper.eq("bill_mode", "3");
                break;
            case "pp":
                queryWrapper.eq("bill_mode", "4");
                break;
            case "so":
                queryWrapper.eq("bill_mode", "5");
                break;
            case "sos":
                queryWrapper.eq("bill_mode", "6");
                break;
            case "si":
                queryWrapper.eq("bill_mode", "7");
                break;
            case "it":
                queryWrapper.eq("bill_mode", "8");
                break;
            case "io":
                queryWrapper.eq("bill_mode", "9");
                break;
            case "ii":
                queryWrapper.eq("bill_mode", "10");
                break;
            case "iia":
                queryWrapper.eq("bill_mode", "11");
                break;
            case "ppi":
                queryWrapper.eq("bill_mode", "12");
                break;
            case "rr":
                queryWrapper.eq("bill_mode", "13");
                break;
            case "dmr":
                queryWrapper.eq("bill_mode", "14");
                break;
            case "dmo":
                queryWrapper.eq("bill_mode", "15");
                break;
            case "ia":
                queryWrapper.eq("bill_mode", "16");
                break;
            case "proo":
                queryWrapper.eq("bill_mode", "17");
                break;
            case "promr":
                queryWrapper.eq("bill_mode", "18");
                break;
            case "promo":
                queryWrapper.eq("bill_mode", "19");
                break;
            case "proi":
                queryWrapper.eq("bill_mode", "20");
                break;
            case "protr":
                queryWrapper.eq("bill_mode", "21");
                break;
            case "proco":
                queryWrapper.eq("bill_mode", "22");
                break;
            case "proci":
                queryWrapper.eq("bill_mode", "23");
                break;
            case "procs":
                queryWrapper.eq("bill_mode", "24");
                break;
            case "procpr":
                queryWrapper.eq("bill_mode", "25");
                break;
            case "pay":
                queryWrapper.eq("bill_mode", "26");
                break;
            case "collect":
                queryWrapper.eq("bill_mode", "27");
                break;
        }
        Page<Bill> page = new Page<Bill>(pageNo, pageSize);
        IPage<Bill> pageList = billService.page(page, queryWrapper);
        for (Bill record : pageList.getRecords()) {
            record.setMerchant(merchantService.getById(record.getClientId()));
            if (record.getInWarehouseId() != null) {
                record.setInWarehouse(warehouseService.getById(record.getInWarehouseId()));
            }
            if (record.getOutWarehouseId() != null) {
                record.setOutWarehouse(warehouseService.getById(record.getOutWarehouseId()));
            }
            if (record.getProductId() != null) {
                record.setProduct(materialService.getById(record.getProductId()));
            }
        }
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param billPage
     * @return
     */
    @AutoLog(value = "单据管理-添加")
    @ApiOperation(value = "单据管理-添加", notes = "单据管理-添加")
    //@RequiresPermissions("bill:byx_bill:add")
    @PostMapping(value = "/add/{billMode}")
    public Result<String> add(@PathVariable("billMode") String billMode, @RequestBody BillPage billPage) {
        Bill bill = new Bill();
        BeanUtils.copyProperties(billPage, bill);
        switch (billMode) {
            case "po":
                bill.setBillMode("1");
                break;
            case "poo":
                bill.setBillMode("2");
                break;
            case "pi":
                bill.setBillMode("3");
                break;
            case "pp":
                bill.setBillMode("4");
                break;
            case "so":
                bill.setBillMode("5");
                break;
            case "sos":
                bill.setBillMode("6");
                break;
            case "si":
                bill.setBillMode("7");
                break;
            case "it":
                bill.setBillMode("8");
                break;
            case "io":
                bill.setBillMode("9");
                break;
            case "ii":
                bill.setBillMode("10");
                break;
            case "iia":
                bill.setBillMode("11");
                break;
            case "ppi":
                bill.setBillMode("12");
                break;
            case "rr":
                bill.setBillMode("13");
                break;
            case "dmr":
                bill.setBillMode("14");
                break;
            case "dmo":
                bill.setBillMode("15");
                break;
            case "ia":
                bill.setBillMode("16");
                break;
            case "proo":
                bill.setBillMode("17");
                break;
            case "promr":
                bill.setBillMode("18");
                break;
            case "promo":
                bill.setBillMode("19");
                break;
            case "proi":
                bill.setBillMode("20");
                break;
            case "protr":
                bill.setBillMode("21");
                break;
            case "proco":
                bill.setBillMode("22");
                break;
            case "proci":
                bill.setBillMode("23");
                break;
            case "procs":
                bill.setBillMode("24");
                break;
            case "procpr":
                bill.setBillMode("25");
                break;
            case "pay":
                bill.setBillMode("26");
                break;
            case "collect":
                bill.setBillMode("27");
                break;
        }
        billService.saveMain(bill, billPage.getBillingDetailList(), billPage.getCollectPayPlanList(), billPage.getPayDetailList(), billPage.getBomRoutingList(), billPage.getCraftDetailList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param billPage
     * @return
     */
    @AutoLog(value = "单据管理-编辑")
    @ApiOperation(value = "单据管理-编辑", notes = "单据管理-编辑")
    //@RequiresPermissions("bill:byx_bill:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody BillPage billPage) {
        Bill bill = new Bill();
        BeanUtils.copyProperties(billPage, bill);
        Bill billEntity = billService.getById(bill.getId());
        if (billEntity == null) {
            return Result.error("未找到对应数据");
        }
        billService.updateMain(bill, billPage.getBillingDetailList(), billPage.getCollectPayPlanList(),
                billPage.getPayDetailList(), billPage.getBomRoutingList(), billPage.getCraftDetailList());
        return Result.OK("编辑成功!");
    }

    @GetMapping("/inventoryByWarehouseAndCode")
    public Result<Integer> inventoryByWarehouseAndCode(@RequestParam(name = "warehouseId", required = true) String warehouseId,
                                                       @RequestParam(name = "code", required = true) String code) {
        int i = billingDetailService.inventoryByWarehouseAndCode(warehouseId, code);
        return Result.OK(i);
    }

    @GetMapping("/getInventory")
    public Result<IPage<BillingDetail>> getInventory(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name = "materialParam", required = false) String materialParam,
                                                     @RequestParam(name = "inWarehouseId", required = false) String inWarehouseId,
                                                     @RequestParam(name = "outWarehouseId", required = false) String outWarehouseId) {
        IPage<BillingDetail> billingDetailIPage = billingDetailService.queryInventory(materialParam, pageNo, pageSize, inWarehouseId, outWarehouseId);
        return Result.OK(billingDetailIPage);
    }

    @GetMapping("/searchRop")
    public Result<IPage<BillingDetail>> searchRop(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "materialCode") String materialCode) {
        IPage<BillingDetail> billingDetailIPage = billingDetailService.queryROPList(materialCode, pageNo, pageSize);
        return Result.OK(billingDetailIPage);
    }

    @GetMapping("/searchMPS")
    public Result<IPage<BillingDetail>> searchMPS(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "materialCode") String materialCode) {
        IPage<BillingDetail> billingDetailIPage = billingDetailService.queryMPSList(materialCode, pageNo, pageSize);
        return Result.OK(billingDetailIPage);
    }

    @GetMapping("/getRop")
    public Result<IPage<BillingDetail>> getROP(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(name = "materialParam", required = false) String materialParam,
                                               @RequestParam(name = "inWarehouseId", required = false) String inWarehouseId,
                                               @RequestParam(name = "outWarehouseId", required = false) String outWarehouseId) {
        IPage<BillingDetail> billingDetailIPage = billingDetailService.queryROP(materialParam, pageNo, pageSize, inWarehouseId, outWarehouseId);
        return Result.OK(billingDetailIPage);
    }

    @GetMapping("/getMPS")
    public Result<IPage<Bill>> getMPS(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<Bill> billIPage = billingDetailService.queryMPS(pageNo, pageSize);
        return Result.OK(billIPage);
    }

    @GetMapping("/getMRP")
    public Result<IPage<BillingDetail>> getMRP(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<BillingDetail> billIPage = billingDetailService.queryMRP(pageNo, pageSize);
        return Result.OK(billIPage);
    }

    @GetMapping("/purchaseReferMRP")
    public Result<IPage<BillingDetail>> purchaseReferMRP(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<BillingDetail> billIPage = billingDetailService.queryPurchaseMRP(pageNo, pageSize);
        return Result.OK(billIPage);
    }

    @GetMapping("/referPoList/{billMode}")
    public Result<IPage<BillingDetail>> queryReferPo(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name = "billCode", required = false) String billCode,
                                                     @RequestParam(name = "clientId", required = false) String clientId,
                                                     @RequestParam(name = "materialParam", required = false) String materialParam,
                                                     @RequestParam(name = "beginTime", required = false) String beginTime,
                                                     @RequestParam(name = "endTime", required = false) String endTime,
                                                     @RequestParam(name = "inWarehouseId", required = false) String inWarehouseId,
                                                     @RequestParam(name = "outWarehouseId", required = false) String outWarehouseId,
                                                     @RequestParam(name = "total", required = false) boolean total,
                                                     @PathVariable("billMode") String billMode) {
        String type = "";
        switch (billMode) {
            case "po":
                type = "1";
                break;
            case "poo":
                type = "2";
                break;
            case "pi":
                type = "3";
                break;
            case "pp":
                type = "4";
                break;
            case "so":
                type = "5";
                break;
            case "sos":
                type = "6";
                break;
            case "si":
                type = "7";
                break;
            case "it":
                type = "8";
                break;
            case "rr":
                type = "13";
                break;
            case "dmr":
                type = "14";
                break;
            case "promr":
                type = "18";
                break;
            case "promo":
                type = "19";
                break;
            case "proo":
                type = "17";
                break;
//            case "itio":
//                type = "28";
//                break;
        }
        IPage<BillingDetail> billingDetailIPage = billingDetailService.queryPoPageList(total, type, billCode, clientId, materialParam, beginTime, endTime, pageNo, pageSize, inWarehouseId, outWarehouseId);
        return Result.OK(billingDetailIPage);
    }

    @GetMapping("/referPIS")
    public Result<IPage<Bill>> queryReferPIS(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(name = "billCode", required = false) String billCode,
                                             @RequestParam(name = "beginTime", required = false) String beginTime,
                                             @RequestParam(name = "endTime", required = false) String endTime,
                                             @RequestParam(name = "total", required = false) boolean total) {
        IPage<Bill> billIPage = billService.queryPISPageList(total, billCode, beginTime, endTime, pageNo, pageSize);
        return Result.OK(billIPage);
    }

    //用于参照单据的工艺明细
    @GetMapping("/referBomRoutingList/{billMode}")
    public Result<IPage<BomRouting>> queryReferBomRouting(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          @RequestParam(name = "billCode", required = false) String billCode,
                                                          @RequestParam(name = "productId", required = false) String productId,
                                                          @RequestParam(name = "beginTime", required = false) String beginTime,
                                                          @RequestParam(name = "endTime", required = false) String endTime,
                                                          @RequestParam(name = "consign", defaultValue = "2") String consign,
                                                          @RequestParam(name = "total", defaultValue = "false") boolean total,
                                                          @PathVariable("billMode") String billMode) {
        String type = "";
        switch (billMode) {
            case "proo":
                type = "17";
                break;
        }
        IPage<BomRouting> bomRoutingIPage = bomRoutingService.queryBomRoutingList(total, type, consign, billCode, productId, beginTime, endTime, pageNo, pageSize);
        return Result.OK(bomRoutingIPage);
    }

    //用于参照单据的工艺明细
    @GetMapping("/referCraftDetailList/{billMode}")
    public Result<IPage<CraftDetail>> queryReferCraftDetail(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            @RequestParam(name = "billCode", required = false) String billCode,
                                                            @RequestParam(name = "clientId", required = false) String clientId,
                                                            @RequestParam(name = "productId", required = false) String productId,
                                                            @RequestParam(name = "beginTime", required = false) String beginTime,
                                                            @RequestParam(name = "endTime", required = false) String endTime,
                                                            @PathVariable("billMode") String billMode) {
        String type = "";
        switch (billMode) {
            case "proco":
                type = "22";
                break;
            case "proci":
                type = "23";
                break;
            case "procs":
                type = "24";
                break;
        }
        IPage<CraftDetail> craftDetailList = craftDetailService.queryCraftDetailList(type, billCode, clientId, productId, beginTime, endTime, pageNo, pageSize);
        return Result.OK(craftDetailList);
    }

    //用于参照采购订单的付款计划
    @GetMapping("/referPoPayList/{billMode}")
    public Result<IPage<CollectPayPlan>> queryReferPoPay(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "billCode", required = false) String billCode,
                                                         @RequestParam(name = "clientId", required = false) String clientId,
                                                         @RequestParam(name = "materialParam", required = false) String materialParam,
                                                         @RequestParam(name = "beginTime", required = false) String beginTime,
                                                         @RequestParam(name = "endTime", required = false) String endTime,
                                                         @RequestParam(name = "total", required = false) boolean total,
                                                         @PathVariable("billMode") String billMode) {
        String type = "";
        switch (billMode) {
            case "po":
                type = "1";
                break;
            case "so":
                type = "5";
                break;
        }
        IPage<CollectPayPlan> CollectPayPlanIPage = collectPayPlanService.queryPoPayPageList(total, type, billCode, clientId, materialParam, beginTime, endTime, pageNo, pageSize);
        return Result.OK(CollectPayPlanIPage);
    }

    //用于参照单据的付款明细
    @GetMapping("/referPayDetailList/{billMode}")
    public Result<IPage<PayDetail>> queryReferPayDetail(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        @RequestParam(name = "billCode", required = false) String billCode,
                                                        @RequestParam(name = "clientId", required = false) String clientId,
                                                        @RequestParam(name = "materialParam", required = false) String materialParam,
                                                        @RequestParam(name = "beginTime", required = false) String beginTime,
                                                        @RequestParam(name = "endTime", required = false) String endTime,
                                                        @RequestParam(name = "total", required = false) boolean total,
                                                        @PathVariable("billMode") String billMode) {
        String type = "";
        switch (billMode) {
            case "pp":
                type = "4";
                break;
            case "procpr":
                type = "25";
                break;
        }
        IPage<PayDetail> PayDetailIPage = payDetailService.queryPayDetailList(total, type, billCode, clientId, materialParam, beginTime, endTime, pageNo, pageSize);
        return Result.OK(PayDetailIPage);
    }

    /**
     * 批量设置状态-审核或者反审核
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态-审核或者反审核")
    public Result<String> batchSetStatus(@RequestBody JSONObject jsonObject,
                                         HttpServletRequest request) {
        String status = jsonObject.getString("status");
        String ids = jsonObject.getString("ids");
        int res = billService.batchSetStatus(status, ids);
        if (res > 0) {
            return Result.OK("删除成功!");
        } else {
            return Result.error("设置失败");
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "单据管理-通过id删除")
    @ApiOperation(value = "单据管理-通过id删除", notes = "单据管理-通过id删除")
    //@RequiresPermissions("bill:byx_bill:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        billService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "单据管理-批量删除")
    @ApiOperation(value = "单据管理-批量删除", notes = "单据管理-批量删除")
    //@RequiresPermissions("bill:byx_bill:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.billService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "单据管理-通过id查询")
    @ApiOperation(value = "单据管理-通过id查询", notes = "单据管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Bill> queryById(@RequestParam(name = "id", required = true) String id) {
        Bill bill = billService.getById(id);
        if (bill == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(bill);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "标的明细-通过主表ID查询")
    @ApiOperation(value = "标的明细-通过主表ID查询", notes = "标的明细-通过主表ID查询")
    @GetMapping(value = "/queryBillingDetailByMainId")
    public Result<IPage<BillingDetail>> queryBillingDetailListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<BillingDetail> billingDetailList = billingDetailService.selectByMainId(id);
        IPage<BillingDetail> page = new Page<>();
        page.setRecords(billingDetailList);
        page.setTotal(billingDetailList.size());
        return Result.OK(page);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "款计划-通过主表ID查询")
    @ApiOperation(value = "款计划-通过主表ID查询", notes = "款计划-通过主表ID查询")
    @GetMapping(value = "/queryCollectPayPlanByMainId")
    public Result<IPage<CollectPayPlan>> queryCollectPayPlanListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<CollectPayPlan> collectPayPlanList = collectPayPlanService.selectByMainId(id);
        IPage<CollectPayPlan> page = new Page<>();
        page.setRecords(collectPayPlanList);
        page.setTotal(collectPayPlanList.size());
        return Result.OK(page);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "付款明细-通过主表ID查询")
    @ApiOperation(value = "付款明细-通过主表ID查询", notes = "付款明细-通过主表ID查询")
    @GetMapping(value = "/queryPayDetailByMainId")
    public Result<IPage<PayDetail>> queryPayDetailListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<PayDetail> payDetailList = payDetailService.selectByMainId(id);
        for (PayDetail payDetail : payDetailList) {
            payDetail.setMerchant(merchantService.getById(payDetail.getClientId()));
        }
        IPage<PayDetail> page = new Page<>();
        page.setRecords(payDetailList);
        page.setTotal(payDetailList.size());
        return Result.OK(page);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "工艺线路-通过主表ID查询")
    @ApiOperation(value = "工艺线路-通过主表ID查询", notes = "工艺线路-通过主表ID查询")
    @GetMapping(value = "/queryBomRoutingByMainId")
    public Result<IPage<BomRouting>> queryBomRoutingListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<BomRouting> bomRoutingList = bomRoutingService.selectByMainId(id);
        IPage<BomRouting> page = new Page<>();
        page.setRecords(bomRoutingList);
        page.setTotal(bomRoutingList.size());
        return Result.OK(page);
    }

    @Autowired
    ICraftService craftService;

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "工艺明细-通过主表ID查询")
    @ApiOperation(value = "工艺明细-通过主表ID查询", notes = "工艺明细-通过主表ID查询")
    @GetMapping(value = "/queryCraftDetailByMainId")
    public Result<IPage<CraftDetail>> queryCraftDetailListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<CraftDetail> craftDetailList = craftDetailService.selectByMainId(id);
        for (CraftDetail craftDetail : craftDetailList) {
            craftDetail.setBomRouting(bomRoutingService.getById(craftDetail.getBomRoutingId()));
            craftDetail.setProduct(materialService.getOne(new QueryWrapper<Material>().eq("code", craftDetail.getProductCode())));
        }
        IPage<CraftDetail> page = new Page<>();
        page.setRecords(craftDetailList);
        page.setTotal(craftDetailList.size());
        return Result.OK(page);
    }

    /**
     * 获取销售统计
     *
     * @return
     */
    @GetMapping("sellInfo")
    public Result<List<Map<String, Object>>> sellInfo() {
        Result<List<Map<String, Object>>> result = new Result<List<Map<String, Object>>>();
        List<Map<String, Object>> list = billService.findSellInfo();
        result.setResult(oConvertUtils.toLowerCasePageList(list));
        return result;
    }

    /**
     * 获取销售额
     *
     * @return
     */
    @GetMapping("getTotalSales")
    public Result<Double> getTotalSales() {
        Result<Double> result = new Result<>();
        result.setResult(billService.getTotalSales());
        return result;
    }

    /**
     * 获取销售订单量
     *
     * @return
     */
    @GetMapping("getSalesOrder")
    public Result<Double> getSalesOrder() {
        Result<Double> result = new Result<>();
        result.setResult(billService.getSalesOrder());
        return result;
    }

    /**
     * 获取收款笔数
     *
     * @return
     */
    @GetMapping("getCollect")
    public Result<Double> getCollect() {
        Result<Double> result = new Result<>();
        result.setResult(billService.getCollect());
        return result;
    }

    /**
     * 获取付款笔数
     *
     * @return
     */
    @GetMapping("getPayment")
    public Result<Double> getPayment() {
        Result<Double> result = new Result<>();
        result.setResult(billService.getPayment());
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param bill
     */
    //@RequiresPermissions("bill:byx_bill:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Bill bill) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<Bill> queryWrapper = QueryGenerator.initQueryWrapper(bill, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //配置选中数据查询条件
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        //Step.2 获取导出数据
        List<Bill> billList = billService.list(queryWrapper);

        // Step.3 组装pageList
        List<BillPage> pageList = new ArrayList<BillPage>();
        for (Bill main : billList) {
            BillPage vo = new BillPage();
            BeanUtils.copyProperties(main, vo);
            List<BillingDetail> billingDetailList = billingDetailService.selectByMainId(main.getId());
            vo.setBillingDetailList(billingDetailList);
            List<CollectPayPlan> collectPayPlanList = collectPayPlanService.selectByMainId(main.getId());
            vo.setCollectPayPlanList(collectPayPlanList);
            List<PayDetail> payDetailList = payDetailService.selectByMainId(main.getId());
            vo.setPayDetailList(payDetailList);
            List<BomRouting> bomRoutingList = bomRoutingService.selectByMainId(main.getId());
            vo.setBomRoutingList(bomRoutingList);
            List<CraftDetail> craftDetailList = craftDetailService.selectByMainId(main.getId());
            vo.setCraftDetailList(craftDetailList);
            pageList.add(vo);
        }
        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "单据管理列表");
        mv.addObject(NormalExcelConstants.CLASS, BillPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("单据管理数据", "导出人:" + sysUser.getRealname(), "单据管理"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    @Autowired
    ISysUserService userService;
    @Autowired
    ISysDepartService departService;

    //采购订单导出模板
    @RequestMapping(value = "/exportXls/po")
    public void exportXlsPO(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\purchaseOrder.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        map.put("estimatedDeliveryDate", bill.getEstimatedDeliveryDate());
        map.put("paymentStyle", bill.getPaymentStyle());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        double a = 0.0;
        double b = 0.0;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("rate", material.getRate());
            lm.put("priceIncludedTax", material.getPriceIncludedTax());
            lm.put("taxIncludedAmount", billingDetail.getTaxIncludedAmount());
            a += billingDetail.getAmount();
            b += billingDetail.getTaxIncludedAmount();
            listMap.add(lm);
        }
        map.put("a", a);
        map.put("b", b);
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //销售订单导出模板
    @RequestMapping(value = "/exportXls/so")
    public void exportXlsSO(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\sellsOrder.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        map.put("estimatedDeliveryDate", bill.getEstimatedDeliveryDate());
        map.put("paymentStyle", bill.getPaymentStyle());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        double a = 0.0;
        double b = 0.0;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("rate", material.getRate());
            lm.put("priceIncludedTax", material.getPriceIncludedTax());
            lm.put("taxIncludedAmount", billingDetail.getTaxIncludedAmount());
            a += billingDetail.getAmount();
            b += billingDetail.getTaxIncludedAmount();
            listMap.add(lm);
        }
        map.put("a", a);
        map.put("b", b);
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }


    //采购入库单导出模板
    @RequestMapping(value = "/exportXls/pib")
    public void exportXlsPIB(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\purchaseInBound.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        map.put("inWarehouseId", warehouseService.getById(bill.getInWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        double a = 0.0;
        double b = 0.0;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("rate", material.getRate());
            lm.put("priceIncludedTax", material.getPriceIncludedTax());
            lm.put("taxIncludedAmount", billingDetail.getTaxIncludedAmount());
            a += billingDetail.getAmount();
            b += billingDetail.getTaxIncludedAmount();
            listMap.add(lm);
        }
        map.put("a", a);
        map.put("b", b);
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //销售出库单导出模板
    @RequestMapping(value = "/exportXls/sos")
    public void exportXlsSOS(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\sellsOutBound.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        map.put("outWarehouseId", warehouseService.getById(bill.getOutWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        double a = 0.0;
        double b = 0.0;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("rate", material.getRate());
            lm.put("priceIncludedTax", material.getPriceIncludedTax());
            lm.put("taxIncludedAmount", billingDetail.getTaxIncludedAmount());
            a += billingDetail.getAmount();
            b += billingDetail.getTaxIncludedAmount();
            listMap.add(lm);
        }
        map.put("a", a);
        map.put("b", b);
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //采购发票导出模板
    @RequestMapping(value = "/exportXls/pi")
    public void exportXlsPI(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\purchaseInvoice.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        map.put("invoiceType", bill.getInvoiceType());
        map.put("invoiceNumber", bill.getInvoiceNumber());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        double a = 0.0;
        double b = 0.0;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("rate", material.getRate());
            lm.put("priceIncludedTax", material.getPriceIncludedTax());
            lm.put("taxIncludedAmount", billingDetail.getTaxIncludedAmount());
            a += billingDetail.getAmount();
            b += billingDetail.getTaxIncludedAmount();
            listMap.add(lm);
        }
        map.put("a", a);
        map.put("b", b);
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //销售开票申请单导出模板
    @RequestMapping(value = "/exportXls/si")
    public void exportXlsSI(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\sellsInvoiceApply.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        map.put("invoiceType", bill.getInvoiceType());
        map.put("invoiceNumber", bill.getInvoiceNumber());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        double a = 0.0;
        double b = 0.0;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("rate", material.getRate());
            lm.put("priceIncludedTax", material.getPriceIncludedTax());
            lm.put("taxIncludedAmount", billingDetail.getTaxIncludedAmount());
            a += billingDetail.getAmount();
            b += billingDetail.getTaxIncludedAmount();
            listMap.add(lm);
        }
        map.put("a", a);
        map.put("b", b);
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //采购付款申请单导出模板
    @RequestMapping(value = "/exportXls/ppa")
    public void exportXlsPPA(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\purchasePayApply.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (PayDetail payDetail : payDetailService.selectByMainId(bill.getId())) {
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("clientId", merchantService.getById(payDetail.getClientId()).getName());
            lm.put("payType", payDetail.getPayType());
            lm.put("applyAmount", payDetail.getApplyAmount());
            lm.put("expectedPayDate", payDetail.getExpectedPayDate());
            lm.put("payAmount", payDetail.getPayAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //库存调拨单导出模板
    @RequestMapping(value = "/exportXls/ia")
    public void exportXlsIA(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\inventoryAllocation.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("inWarehouseId", warehouseService.getById(bill.getInWarehouseId()).getWarehouseName());
        map.put("outWarehouseId", warehouseService.getById(bill.getOutWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //调拨出库单导出模板
    @RequestMapping(value = "/exportXls/ao")
    public void exportXlsAO(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\allocationOut.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("outWarehouseId", warehouseService.getById(bill.getOutWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //调拨入库单导出模板
    @RequestMapping(value = "/exportXls/ai")
    public void exportXlsAI(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\allocationIn.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("inWarehouseId", warehouseService.getById(bill.getInWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //甲方供料入库单导出模板
    @RequestMapping(value = "/exportXls/iiba")
    public void exportXlsIIBA(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\inventoryInByA.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        map.put("inWarehouseId", warehouseService.getById(bill.getInWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //零星采购入库单导出模板
    @RequestMapping(value = "/exportXls/ppis")
    public void exportXlsPPIS(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\ppInStock.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("inWarehouseId", warehouseService.getById(bill.getInWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //部门领料申请单导出模板
    @RequestMapping(value = "/exportXls/dmr")
    public void exportXlsDMR(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\depMatRequset.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("demandedDate", bill.getDemandedDate());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //部门领料出库单导出模板
    @RequestMapping(value = "/exportXls/dmo")
    public void exportXlsDMO(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\depMatOut.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("outWarehouseId", warehouseService.getById(bill.getOutWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //补料申请单导出模板
    @RequestMapping(value = "/exportXls/rr")
    public void exportXlsRR(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\replenishReq.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("demandedDate", bill.getDemandedDate());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        double a = 0.0;
        double b = 0.0;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("priceIncludedTax", material.getPriceIncludedTax());
            lm.put("taxIncludedAmount", billingDetail.getTaxIncludedAmount());
            a += billingDetail.getAmount();
            b += billingDetail.getTaxIncludedAmount();
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        map.put("a", a);
        map.put("b", b);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产订单导出模板
    @Autowired
    IBomService bomService;

    @RequestMapping(value = "/exportXls/proo")
    public void exportXlsPROO(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\produceorder.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("productId", materialService.getById(bill.getProductId()).getMaterialName());
        map.put("bomId", bomService.getById(bill.getBomId()).getBomName());
        map.put("unitLoad", materialService.getById(bill.getProductId()).getUnitLoad());
        map.put("produceNumber", bill.getProduceNumber());
        map.put("demandedDate", bill.getDemandedDate());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("standardDosage", billingDetail.getNumerator() / billingDetail.getDenominator() * bill.getProduceNumber());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        List<Map<String, Object>> listMap2 = new ArrayList<Map<String, Object>>();
        int no2 = 1;
        for (BomRouting bomRouting : bomRoutingService.selectByMainId(bill.getId())) {
//            BomRouting bomRouting = bomRoutingService.getOne(new QueryWrapper<BomRouting>().eq("craftCode", bomRouting.getCraftCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no2", no2++);
            lm.put("craftCode", bomRouting.getCraftCode());
            lm.put("craftName", bomRouting.getCraftName());
            lm.put("craftTime", bomRouting.getCraftTime());
            lm.put("craftCost", bomRouting.getCraftCost());
            lm.put("processCost", bomRouting.getProcessCost());
            lm.put("wastePrice", bomRouting.getWastePrice());
            lm.put("wagePrice", bomRouting.getWagePrice());
            listMap2.add(lm);
        }
        map.put("maplist2", listMap2);

        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产领料申请单导出模板
    @RequestMapping(value = "/exportXls/pmr")
    public void exportXlsPMR(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\proMatReq.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("demandedDate", bill.getDemandedDate());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产领料出库单导出模板
    @RequestMapping(value = "/exportXls/pmo")
    public void exportXlsPMO(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\proMatOut.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("outWarehouseId", warehouseService.getById(bill.getOutWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产入库单导出模板
    @RequestMapping(value = "/exportXls/proi")
    public void exportXlsPROI(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\proIn.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("inWarehouseId", warehouseService.getById(bill.getInWarehouseId()).getWarehouseName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (BillingDetail billingDetail : billingDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("materialCode", billingDetail.getMaterialCode());
            lm.put("materialName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("unit", material.getUnit());
            lm.put("count", billingDetail.getCount());
            lm.put("price", material.getPrice());
            lm.put("amount", billingDetail.getAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产工时记录单导出模板
    @RequestMapping(value = "/exportXls/ptr")
    public void exportXlsPTR(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\proTimeRecord.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (CraftDetail craftDetail : craftDetailService.selectByMainId(bill.getId())) {
//            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", billingDetail.getMaterialCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            craftService.getOne(new QueryWrapper<Craft>().eq("craft_code", craftDetail.getCraftCode()));
            lm.put("no", no++);
            lm.put("craftCode", craftDetail.getCraftCode());
            lm.put("craftName", bomRoutingService.getById(craftDetail.getBomRoutingId()).getCraftName());
            lm.put("processCost", bomRoutingService.getById(craftDetail.getBomRoutingId()).getProcessCost());
            lm.put("wastePrice", bomRoutingService.getById(craftDetail.getBomRoutingId()).getWastePrice());
            lm.put("wagePrice", bomRoutingService.getById(craftDetail.getBomRoutingId()).getWagePrice());
            lm.put("completeQuantity", craftDetail.getCompleteQuantity());
            lm.put("scrapQuantity", craftDetail.getScrapQuantity());
            lm.put("wasteQuantity", craftDetail.getWasteQuantity());
            lm.put("craftAmount", craftDetail.getCraftAmount());
            lm.put("wasteAmount", craftDetail.getWasteAmount());
            lm.put("totalAmount", craftDetail.getTotalAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产委托出库单导出模板
    @RequestMapping(value = "/exportXls/pco")
    public void exportXlsPCO(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\proConsignOut.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (CraftDetail craftDetail : craftDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", craftDetail.getProductCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            craftService.getOne(new QueryWrapper<Craft>().eq("craft_code", craftDetail.getCraftCode()));
            lm.put("no", no++);
            lm.put("craftCode", craftDetail.getCraftCode());
            lm.put("craftName", bomRoutingService.getById(craftDetail.getBomRoutingId()).getCraftName());
            lm.put("productCode", craftDetail.getProductCode());
            lm.put("productName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("processCost", bomRoutingService.getById(craftDetail.getBomRoutingId()).getProcessCost());
            lm.put("wastePrice", bomRoutingService.getById(craftDetail.getBomRoutingId()).getWastePrice());
            lm.put("wagePrice", bomRoutingService.getById(craftDetail.getBomRoutingId()).getWagePrice());
            lm.put("consignQuantity", craftDetail.getConsignQuantity());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产委托入库单导出模板
    @RequestMapping(value = "/exportXls/pci")
    public void exportXlsPCI(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\proConsignIn.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (CraftDetail craftDetail : craftDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", craftDetail.getProductCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            craftService.getOne(new QueryWrapper<Craft>().eq("craft_code", craftDetail.getCraftCode()));
            lm.put("no", no++);
            lm.put("craftCode", craftDetail.getCraftCode());
            lm.put("craftName", bomRoutingService.getById(craftDetail.getBomRoutingId()).getCraftName());
            lm.put("productCode", craftDetail.getProductCode());
            lm.put("productName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("completeQuantity", craftDetail.getCompleteQuantity());
            lm.put("wasteQuantity", craftDetail.getWasteQuantity());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产委托结算单导出模板
    @RequestMapping(value = "/exportXls/pcs")
    public void exportXlsPCS(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\proConsignSettle.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("clientId", merchantService.getById(bill.getClientId()).getName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (CraftDetail craftDetail : craftDetailService.selectByMainId(bill.getId())) {
            Material material = materialService.getOne(new QueryWrapper<Material>().eq("code", craftDetail.getProductCode()));
            Map<String, Object> lm = new HashMap<String, Object>();
            craftService.getOne(new QueryWrapper<Craft>().eq("craft_code", craftDetail.getCraftCode()));
            lm.put("no", no++);
            lm.put("craftCode", craftDetail.getCraftCode());
            lm.put("craftName", bomRoutingService.getById(craftDetail.getBomRoutingId()).getCraftName());
            lm.put("productCode", craftDetail.getProductCode());
            lm.put("productName", material.getMaterialName());
            lm.put("size", material.getSize());
            lm.put("processCost", bomRoutingService.getById(craftDetail.getBomRoutingId()).getProcessCost());
            lm.put("wagePrice", bomRoutingService.getById(craftDetail.getBomRoutingId()).getWagePrice());
            lm.put("craftAmount", craftDetail.getCraftAmount());
            lm.put("wasteAmount", craftDetail.getWasteAmount());
            lm.put("totalAmount", craftDetail.getTotalAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //生产委托付款申请单导出模板
    @RequestMapping(value = "/exportXls/pcpr")
    public void exportXlsPCPR(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\proConsignPayReq.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (PayDetail payDetail : payDetailService.selectByMainId(bill.getId())) {
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("clientId", merchantService.getById(payDetail.getClientId()).getName());
            lm.put("payType", payDetail.getPayType());
            lm.put("applyAmount", payDetail.getApplyAmount());
            lm.put("expectedPayDate", payDetail.getExpectedPayDate());
            lm.put("payAmount", payDetail.getPayAmount());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //付款单导出模板
    @RequestMapping(value = "/exportXls/pay")
    public void exportXlsPAY(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\pay.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("accountPayment", bill.getAccountPayment());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (PayDetail payDetail : payDetailService.selectByMainId(bill.getId())) {
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("clientId", merchantService.getById(payDetail.getClientId()).getName());
            lm.put("payType", payDetail.getPayType());
            lm.put("applyAmount", payDetail.getApplyAmount());
            lm.put("payAmount", payDetail.getPayAmount());
            lm.put("bankType", merchantService.getById(payDetail.getClientId()).getBankType());
            lm.put("bankName", merchantService.getById(payDetail.getClientId()).getBankName());
            lm.put("bankAccount", merchantService.getById(payDetail.getClientId()).getBankAccount());
            lm.put("bankNum", merchantService.getById(payDetail.getClientId()).getBankNum());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }

    //收款单导出模板
    @RequestMapping(value = "/exportXls/collect")
    public void exportXlsCOLLECT(HttpServletResponse res, HttpServletRequest req) throws IOException {
        TemplateExportParams params = new TemplateExportParams("D:\\1MyProject\\DBC_ERP\\jeecg-boot\\jeecg-module-demo\\src\\main\\resources\\templates\\excel\\collect.xlsx");
        Map<String, Object> map = new HashMap<String, Object>();
        Bill bill = billService.getById(req.getParameter("selections"));
        map.put("createTime", bill.getCreateTime());
        map.put("createBy", bill.getCreateBy());
        map.put("code", bill.getCode());
        map.put("billDate", bill.getBillDate());
        map.put("personName", userService.getOne(new QueryWrapper<SysUser>().eq("username", bill.getPersonName())).getRealname());
        map.put("departmentName", departService.getById(bill.getDepartmentName()).getDepartName());
        map.put("accountPayment", bill.getAccountPayment());
        if (bill.getRemark() != null) {
            map.put("remark", bill.getRemark());
        } else {
            map.put("remark", "");
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int no = 1;
        for (PayDetail payDetail : payDetailService.selectByMainId(bill.getId())) {
            Map<String, Object> lm = new HashMap<String, Object>();
            lm.put("no", no++);
            lm.put("clientId", merchantService.getById(payDetail.getClientId()).getName());
            lm.put("payType", payDetail.getPayType());
            lm.put("applyAmount", payDetail.getApplyAmount());
            lm.put("payAmount", payDetail.getPayAmount());
            lm.put("remark", payDetail.getRemark());
            listMap.add(lm);
        }
        map.put("maplist", listMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        ServletOutputStream ops = res.getOutputStream();
        workbook.write(ops);
        ops.close();
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("bill:byx_bill:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<BillPage> list = ExcelImportUtil.importExcel(file.getInputStream(), BillPage.class, params);
                for (BillPage page : list) {
                    Bill po = new Bill();
                    BeanUtils.copyProperties(page, po);
                    billService.saveMain(po, page.getBillingDetailList(), page.getCollectPayPlanList(), page.getPayDetailList(), page.getBomRoutingList(), page.getCraftDetailList());
                }
                return Result.OK("文件导入成功！数据行数:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.OK("文件导入失败！");
    }

}
