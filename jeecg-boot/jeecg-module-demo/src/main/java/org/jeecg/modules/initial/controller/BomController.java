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

import org.jeecg.modules.bill.entity.BomRouting;
import org.jeecg.modules.bill.service.IBomRoutingService;
import org.jeecg.modules.initial.entity.Material;
import org.jeecg.modules.initial.service.IMaterialService;
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
import org.jeecg.modules.initial.entity.BomSubroutine;
import org.jeecg.modules.initial.entity.Bom;
import org.jeecg.modules.initial.vo.BomPage;
import org.jeecg.modules.initial.service.IBomService;
import org.jeecg.modules.initial.service.IBomSubroutineService;
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
 * @Description: 物料清单
 * @Author: jeecg-boot
 * @Date: 2023-05-01
 * @Version: V1.0
 */
@Api(tags = "物料清单")
@RestController
@RequestMapping("/initial/bom")
@Slf4j
public class BomController {
    @Autowired
    private IBomService bomService;
    @Autowired
    private IBomSubroutineService bomSubroutineService;
    @Autowired
    private IMaterialService materialService;
    @Autowired
    private IBomRoutingService bomRoutingService;

    /**
     * 分页列表查询
     *
     * @param bom
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "物料清单-分页列表查询")
    @ApiOperation(value = "物料清单-分页列表查询", notes = "物料清单-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Bom>> queryPageList(Bom bom,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                            HttpServletRequest req) {
        QueryWrapper<Bom> queryWrapper = QueryGenerator.initQueryWrapper(bom, req.getParameterMap());
        Page<Bom> page = new Page<Bom>(pageNo, pageSize);
        IPage<Bom> pageList = bomService.page(page, queryWrapper);
        for (Bom record : pageList.getRecords()) {
            if (record.getProdId() != null) {
                record.setMaterial(materialService.getById(record.getProdId()));
            }
        }
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param bomPage
     * @return
     */
    @AutoLog(value = "物料清单-添加")
    @ApiOperation(value = "物料清单-添加", notes = "物料清单-添加")
    //@RequiresPermissions("initial:byx_bom:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody BomPage bomPage) {
        Bom bom = new Bom();
        BeanUtils.copyProperties(bomPage, bom);
        if (bomService.saveMain(bom, bomPage.getBomSubroutineList(), bomPage.getBomRoutingList())) {
            return Result.OK("添加成功！");
        } else {
            return Result.error("每个产品只能对应一个默认物料清单");
        }
    }

    /**
     * 编辑
     *
     * @param bomPage
     * @return
     */
    @AutoLog(value = "物料清单-编辑")
    @ApiOperation(value = "物料清单-编辑", notes = "物料清单-编辑")
    //@RequiresPermissions("initial:byx_bom:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody BomPage bomPage) {
        Bom bom = new Bom();
        BeanUtils.copyProperties(bomPage, bom);
        Bom bomEntity = bomService.getById(bom.getId());
        if (bomEntity == null) {
            return Result.error("未找到对应数据");
        }
        if (bomService.updateMain(bom, bomPage.getBomSubroutineList(),bomPage.getBomRoutingList())) {
            return Result.OK("编辑成功！");
        } else {
            return Result.error("默认物料清单数量只能为1");
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "物料清单-通过id删除")
    @ApiOperation(value = "物料清单-通过id删除", notes = "物料清单-通过id删除")
    //@RequiresPermissions("initial:byx_bom:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        bomService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "物料清单-批量删除")
    @ApiOperation(value = "物料清单-批量删除", notes = "物料清单-批量删除")
    //@RequiresPermissions("initial:byx_bom:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.bomService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "物料清单-通过id查询")
    @ApiOperation(value = "物料清单-通过id查询", notes = "物料清单-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Bom> queryById(@RequestParam(name = "id", required = true) String id) {
        Bom bom = bomService.getById(id);
        if (bom == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(bom);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "子件明细-通过主表ID查询")
    @ApiOperation(value = "子件明细-通过主表ID查询", notes = "子件明细-通过主表ID查询")
    @GetMapping(value = "/queryBomSubroutineByMainId")
    public Result<IPage<BomSubroutine>> queryBomSubroutineListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<BomSubroutine> bomSubroutineList = bomSubroutineService.selectByMainId(id);
        for (BomSubroutine bomSubroutine : bomSubroutineList) {
            if (bomSubroutine.getCode()!=null) {
                bomSubroutine.setMaterial(materialService.getOne(new QueryWrapper<Material>().eq("code", bomSubroutine.getCode())));
            }
        }
        IPage<BomSubroutine> page = new Page<>();
        page.setRecords(bomSubroutineList);
        page.setTotal(bomSubroutineList.size());
        return Result.OK(page);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "子件明细-通过主表ID查询")
    @ApiOperation(value = "工艺明细-通过主表ID查询", notes = "子件明细-通过主表ID查询")
    @GetMapping(value = "/queryBomRoutingByBomId")
    public Result<IPage<BomRouting>> queryBomRoutingListByBomId(@RequestParam(name = "id", required = true) String id) {
        List<BomRouting> bomRoutingList = bomRoutingService.selectByBomId(id);
        IPage<BomRouting> page = new Page<>();
        page.setRecords(bomRoutingList);
        page.setTotal(bomRoutingList.size());
        return Result.OK(page);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param bom
     */
    //@RequiresPermissions("initial:byx_bom:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Bom bom) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<Bom> queryWrapper = QueryGenerator.initQueryWrapper(bom, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //配置选中数据查询条件
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        //Step.2 获取导出数据
        List<Bom> bomList = bomService.list(queryWrapper);

        // Step.3 组装pageList
        List<BomPage> pageList = new ArrayList<BomPage>();
        for (Bom main : bomList) {
            BomPage vo = new BomPage();
            BeanUtils.copyProperties(main, vo);
            List<BomSubroutine> bomSubroutineList = bomSubroutineService.selectByMainId(main.getId());
            List<BomRouting> bomRoutingList = bomRoutingService.selectByBomId(main.getId());
            vo.setBomSubroutineList(bomSubroutineList);
            vo.setBomRoutingList(bomRoutingList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "物料清单列表");
        mv.addObject(NormalExcelConstants.CLASS, BomPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("物料清单数据", "导出人:" + sysUser.getRealname(), "物料清单"));
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
    //@RequiresPermissions("initial:byx_bom:importExcel")
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
                List<BomPage> list = ExcelImportUtil.importExcel(file.getInputStream(), BomPage.class, params);
                for (BomPage page : list) {
                    Bom po = new Bom();
                    BeanUtils.copyProperties(page, po);
                    bomService.saveMain(po, page.getBomSubroutineList(),page.getBomRoutingList());
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
