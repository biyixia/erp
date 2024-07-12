package org.jeecg.modules.initial.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.initial.entity.Customer;
import org.jeecg.modules.initial.entity.Supplier;
import org.jeecg.modules.initial.entity.Merchant;
import org.jeecg.modules.initial.vo.MerchantPage;
import org.jeecg.modules.initial.service.IMerchantService;
import org.jeecg.modules.initial.service.ICustomerService;
import org.jeecg.modules.initial.service.ISupplierService;
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
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 客商管理
 * @Author: jeecg-boot
 * @Date: 2023-03-27
 * @Version: V1.0
 */
@Api(tags = "客商管理")
@RestController
@RequestMapping("/initial/merchant")
@Slf4j
public class MerchantController {
    @Autowired
    private IMerchantService merchantService;
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private ISupplierService supplierService;

    /**
     * 分页列表查询
     *
     * @param merchant
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "客商管理-分页列表查询")
    @ApiOperation(value = "客商管理-分页列表查询", notes = "客商管理-分页列表查询")
    @GetMapping(value = {"/list/{type}","/list"})
    public Result<IPage<Merchant>> queryPageList(Merchant merchant,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req,
                                                 @PathVariable(value = "type",required = false) String type) {
        QueryWrapper<Merchant> queryWrapper = QueryGenerator.initQueryWrapper(merchant, req.getParameterMap());
        if (type != null) {
            switch (type) {
                case "customer":
                    queryWrapper.eq("is_customer", "Y");
                    break;
                case "provider":
                    queryWrapper.eq("is_supplier", "Y");
                    break;
                default:;
            }
        }
        Map<String, String[]> parameterMap = req.getParameterMap();
        String[] contactParams = parameterMap.get("contactParam");
        if (contactParams != null) {
            for (String contactParam : contactParams) {
                if (contactParam != null) {
                    queryWrapper.like("contacts", contactParam).or().eq("phone_num", contactParam);
                }
            }
        }
        Page<Merchant> page = new Page<Merchant>(pageNo, pageSize);
        IPage<Merchant> pageList = merchantService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param merchantPage
     * @return
     */
    @AutoLog(value = "客商管理-添加")
    @ApiOperation(value = "客商管理-添加", notes = "客商管理-添加")
    //@RequiresPermissions("initial:byx_merchant:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody MerchantPage merchantPage) {
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(merchantPage, merchant);
        merchantService.saveMain(merchant, merchantPage.getCustomerList(), merchantPage.getSupplierList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param merchantPage
     * @return
     */
    @AutoLog(value = "客商管理-编辑")
    @ApiOperation(value = "客商管理-编辑", notes = "客商管理-编辑")
    //@RequiresPermissions("initial:byx_merchant:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody MerchantPage merchantPage) {
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(merchantPage, merchant);
        Merchant merchantEntity = merchantService.getById(merchant.getId());
        if (merchantEntity == null) {
            return Result.error("未找到对应数据");
        }
        merchantService.updateMain(merchant, merchantPage.getCustomerList(), merchantPage.getSupplierList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "客商管理-通过id删除")
    @ApiOperation(value = "客商管理-通过id删除", notes = "客商管理-通过id删除")
    //@RequiresPermissions("initial:byx_merchant:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        merchantService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "客商管理-批量删除")
    @ApiOperation(value = "客商管理-批量删除", notes = "客商管理-批量删除")
    //@RequiresPermissions("initial:byx_merchant:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.merchantService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "客商管理-通过id查询")
    @ApiOperation(value = "客商管理-通过id查询", notes = "客商管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Merchant> queryById(@RequestParam(name = "id", required = true) String id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(merchant);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "客户补充信息-通过主表ID查询")
    @ApiOperation(value = "客户补充信息-通过主表ID查询", notes = "客户补充信息-通过主表ID查询")
    @GetMapping(value = "/queryCustomerByMainId")
    public Result<IPage<Customer>> queryCustomerListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<Customer> customerList = customerService.selectByMainId(id);
        IPage<Customer> page = new Page<>();
        page.setRecords(customerList);
        page.setTotal(customerList.size());
        return Result.OK(page);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "供应商补充信息-通过主表ID查询")
    @ApiOperation(value = "供应商补充信息-通过主表ID查询", notes = "供应商补充信息-通过主表ID查询")
    @GetMapping(value = "/querySupplierByMainId")
    public Result<IPage<Supplier>> querySupplierListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<Supplier> supplierList = supplierService.selectByMainId(id);
        IPage<Supplier> page = new Page<>();
        page.setRecords(supplierList);
        page.setTotal(supplierList.size());
        return Result.OK(page);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param merchant
     */
    //@RequiresPermissions("initial:byx_merchant:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Merchant merchant) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<Merchant> queryWrapper = QueryGenerator.initQueryWrapper(merchant, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //配置选中数据查询条件
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        //Step.2 获取导出数据
        List<Merchant> merchantList = merchantService.list(queryWrapper);

        // Step.3 组装pageList
        List<MerchantPage> pageList = new ArrayList<MerchantPage>();
        for (Merchant main : merchantList) {
            MerchantPage vo = new MerchantPage();
            BeanUtils.copyProperties(main, vo);
            List<Customer> customerList = customerService.selectByMainId(main.getId());
            vo.setCustomerList(customerList);
            List<Supplier> supplierList = supplierService.selectByMainId(main.getId());
            vo.setSupplierList(supplierList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "客商管理列表");
        mv.addObject(NormalExcelConstants.CLASS, MerchantPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("客商管理数据", "导出人:" + sysUser.getRealname(), "客商管理"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("initial:byx_merchant:importExcel")
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
                List<MerchantPage> list = ExcelImportUtil.importExcel(file.getInputStream(), MerchantPage.class, params);
                for (MerchantPage page : list) {
                    Merchant po = new Merchant();
                    BeanUtils.copyProperties(page, po);
                    merchantService.saveMain(po, page.getCustomerList(), page.getSupplierList());
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
