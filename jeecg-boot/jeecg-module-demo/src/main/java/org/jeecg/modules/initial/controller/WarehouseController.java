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
import org.jeecg.modules.initial.entity.Warehouse;
import org.jeecg.modules.initial.vo.WarehousePage;
import org.jeecg.modules.initial.service.IWarehouseService;
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
 * @Description: 仓库档案
 * @Author: jeecg-boot
 * @Date: 2023-04-29
 * @Version: V1.0
 */
@Api(tags = "仓库档案")
@RestController
@RequestMapping("/initial/warehouse")
@Slf4j
public class WarehouseController {
    @Autowired
    private IWarehouseService warehouseService;

    /**
     * 分页列表查询
     *
     * @param warehouse
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "仓库档案-分页列表查询")
    @ApiOperation(value = "仓库档案-分页列表查询", notes = "仓库档案-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Warehouse>> queryPageList(Warehouse warehouse,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        QueryWrapper<Warehouse> queryWrapper = QueryGenerator.initQueryWrapper(warehouse, req.getParameterMap());
        if (req.getParameter("ownership") != null && req.getParameter("ownership").equals("供应商仓库")) {
            queryWrapper.or().eq("ownership", "公司仓库");
        }
        Page<Warehouse> page = new Page<Warehouse>(pageNo, pageSize);
        IPage<Warehouse> pageList = warehouseService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param warehousePage
     * @return
     */
    @AutoLog(value = "仓库档案-添加")
    @ApiOperation(value = "仓库档案-添加", notes = "仓库档案-添加")
    //@RequiresPermissions("initial:byx_warehouse:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody WarehousePage warehousePage) {
        Warehouse warehouse = new Warehouse();
        BeanUtils.copyProperties(warehousePage, warehouse);
        warehouseService.saveMain(warehouse);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param warehousePage
     * @return
     */
    @AutoLog(value = "仓库档案-编辑")
    @ApiOperation(value = "仓库档案-编辑", notes = "仓库档案-编辑")
    //@RequiresPermissions("initial:byx_warehouse:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody WarehousePage warehousePage) {
        Warehouse warehouse = new Warehouse();
        BeanUtils.copyProperties(warehousePage, warehouse);
        Warehouse warehouseEntity = warehouseService.getById(warehouse.getId());
        if (warehouseEntity == null) {
            return Result.error("未找到对应数据");
        }
        warehouseService.updateMain(warehouse);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "仓库档案-通过id删除")
    @ApiOperation(value = "仓库档案-通过id删除", notes = "仓库档案-通过id删除")
    //@RequiresPermissions("initial:byx_warehouse:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        warehouseService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "仓库档案-批量删除")
    @ApiOperation(value = "仓库档案-批量删除", notes = "仓库档案-批量删除")
    //@RequiresPermissions("initial:byx_warehouse:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.warehouseService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "仓库档案-通过id查询")
    @ApiOperation(value = "仓库档案-通过id查询", notes = "仓库档案-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Warehouse> queryById(@RequestParam(name = "id", required = true) String id) {
        Warehouse warehouse = warehouseService.getById(id);
        if (warehouse == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(warehouse);

    }


    /**
     * 导出excel
     *
     * @param request
     * @param warehouse
     */
    //@RequiresPermissions("initial:byx_warehouse:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Warehouse warehouse) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<Warehouse> queryWrapper = QueryGenerator.initQueryWrapper(warehouse, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //配置选中数据查询条件
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        //Step.2 获取导出数据
        List<Warehouse> warehouseList = warehouseService.list(queryWrapper);

        // Step.3 组装pageList
        List<WarehousePage> pageList = new ArrayList<WarehousePage>();
        for (Warehouse main : warehouseList) {
            WarehousePage vo = new WarehousePage();
            BeanUtils.copyProperties(main, vo);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "仓库档案列表");
        mv.addObject(NormalExcelConstants.CLASS, WarehousePage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("仓库档案数据", "导出人:" + sysUser.getRealname(), "仓库档案"));
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
    //@RequiresPermissions("initial:byx_warehouse:importExcel")
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
                List<WarehousePage> list = ExcelImportUtil.importExcel(file.getInputStream(), WarehousePage.class, params);
                for (WarehousePage page : list) {
                    Warehouse po = new Warehouse();
                    BeanUtils.copyProperties(page, po);
                    warehouseService.saveMain(po);
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
