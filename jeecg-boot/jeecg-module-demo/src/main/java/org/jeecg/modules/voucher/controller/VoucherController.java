package org.jeecg.modules.voucher.controller;

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
import org.jeecg.modules.voucher.entity.EntryDetail;
import org.jeecg.modules.voucher.entity.Voucher;
import org.jeecg.modules.voucher.vo.VoucherPage;
import org.jeecg.modules.voucher.service.IVoucherService;
import org.jeecg.modules.voucher.service.IEntryDetailService;
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
 * @Description: 填制凭证
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@Api(tags="填制凭证")
@RestController
@RequestMapping("/voucher/voucher")
@Slf4j
public class VoucherController {
	@Autowired
	private IVoucherService voucherService;
	@Autowired
	private IEntryDetailService entryDetailService;
	
	/**
	 * 分页列表查询
	 *
	 * @param voucher
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "填制凭证-分页列表查询")
	@ApiOperation(value="填制凭证-分页列表查询", notes="填制凭证-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Voucher>> queryPageList(Voucher voucher,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Voucher> queryWrapper = QueryGenerator.initQueryWrapper(voucher, req.getParameterMap());
		Page<Voucher> page = new Page<Voucher>(pageNo, pageSize);
		IPage<Voucher> pageList = voucherService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param voucherPage
	 * @return
	 */
	@AutoLog(value = "填制凭证-添加")
	@ApiOperation(value="填制凭证-添加", notes="填制凭证-添加")
    //@RequiresPermissions("voucher:byx_voucher:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody VoucherPage voucherPage) {
		Voucher voucher = new Voucher();
		BeanUtils.copyProperties(voucherPage, voucher);
		voucherService.saveMain(voucher, voucherPage.getEntryDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param voucherPage
	 * @return
	 */
	@AutoLog(value = "填制凭证-编辑")
	@ApiOperation(value="填制凭证-编辑", notes="填制凭证-编辑")
    //@RequiresPermissions("voucher:byx_voucher:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody VoucherPage voucherPage) {
		Voucher voucher = new Voucher();
		BeanUtils.copyProperties(voucherPage, voucher);
		Voucher voucherEntity = voucherService.getById(voucher.getId());
		if(voucherEntity==null) {
			return Result.error("未找到对应数据");
		}
		voucherService.updateMain(voucher, voucherPage.getEntryDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "填制凭证-通过id删除")
	@ApiOperation(value="填制凭证-通过id删除", notes="填制凭证-通过id删除")
    //@RequiresPermissions("voucher:byx_voucher:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		voucherService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "填制凭证-批量删除")
	@ApiOperation(value="填制凭证-批量删除", notes="填制凭证-批量删除")
    //@RequiresPermissions("voucher:byx_voucher:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.voucherService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "填制凭证-通过id查询")
	@ApiOperation(value="填制凭证-通过id查询", notes="填制凭证-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Voucher> queryById(@RequestParam(name="id",required=true) String id) {
		Voucher voucher = voucherService.getById(id);
		if(voucher==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(voucher);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "分录明细-通过主表ID查询")
	@ApiOperation(value="分录明细-通过主表ID查询", notes="分录明细-通过主表ID查询")
	@GetMapping(value = "/queryEntryDetailByMainId")
	public Result<IPage<EntryDetail>> queryEntryDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<EntryDetail> entryDetailList = entryDetailService.selectByMainId(id);
		IPage <EntryDetail> page = new Page<>();
		page.setRecords(entryDetailList);
		page.setTotal(entryDetailList.size());
		return Result.OK(page);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param voucher
    */
    //@RequiresPermissions("voucher:byx_voucher:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Voucher voucher) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<Voucher> queryWrapper = QueryGenerator.initQueryWrapper(voucher, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

     //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
           List<String> selectionList = Arrays.asList(selections.split(","));
           queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<Voucher>  voucherList = voucherService.list(queryWrapper);

      // Step.3 组装pageList
      List<VoucherPage> pageList = new ArrayList<VoucherPage>();
      for (Voucher main : voucherList) {
          VoucherPage vo = new VoucherPage();
          BeanUtils.copyProperties(main, vo);
          List<EntryDetail> entryDetailList = entryDetailService.selectByMainId(main.getId());
          vo.setEntryDetailList(entryDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "填制凭证列表");
      mv.addObject(NormalExcelConstants.CLASS, VoucherPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("填制凭证数据", "导出人:"+sysUser.getRealname(), "填制凭证"));
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
    //@RequiresPermissions("voucher:byx_voucher:importExcel")
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
              List<VoucherPage> list = ExcelImportUtil.importExcel(file.getInputStream(), VoucherPage.class, params);
              for (VoucherPage page : list) {
                  Voucher po = new Voucher();
                  BeanUtils.copyProperties(page, po);
                  voucherService.saveMain(po, page.getEntryDetailList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
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
