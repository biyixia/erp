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
import org.jeecg.modules.voucher.entity.CertificateTemplate;
import org.jeecg.modules.voucher.vo.CertificateTemplatePage;
import org.jeecg.modules.voucher.service.ICertificateTemplateService;
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
 * @Description: 凭证模板
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@Api(tags="凭证模板")
@RestController
@RequestMapping("/voucher/certificateTemplate")
@Slf4j
public class CertificateTemplateController {
	@Autowired
	private ICertificateTemplateService certificateTemplateService;
	@Autowired
	private IEntryDetailService entryDetailService;
	
	/**
	 * 分页列表查询
	 *
	 * @param certificateTemplate
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "凭证模板-分页列表查询")
	@ApiOperation(value="凭证模板-分页列表查询", notes="凭证模板-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CertificateTemplate>> queryPageList(CertificateTemplate certificateTemplate,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CertificateTemplate> queryWrapper = QueryGenerator.initQueryWrapper(certificateTemplate, req.getParameterMap());
		Page<CertificateTemplate> page = new Page<CertificateTemplate>(pageNo, pageSize);
		IPage<CertificateTemplate> pageList = certificateTemplateService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param certificateTemplatePage
	 * @return
	 */
	@AutoLog(value = "凭证模板-添加")
	@ApiOperation(value="凭证模板-添加", notes="凭证模板-添加")
    //@RequiresPermissions("voucher:byx_certificate_template:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CertificateTemplatePage certificateTemplatePage) {
		CertificateTemplate certificateTemplate = new CertificateTemplate();
		BeanUtils.copyProperties(certificateTemplatePage, certificateTemplate);
		certificateTemplateService.saveMain(certificateTemplate, certificateTemplatePage.getEntryDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param certificateTemplatePage
	 * @return
	 */
	@AutoLog(value = "凭证模板-编辑")
	@ApiOperation(value="凭证模板-编辑", notes="凭证模板-编辑")
    //@RequiresPermissions("voucher:byx_certificate_template:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CertificateTemplatePage certificateTemplatePage) {
		CertificateTemplate certificateTemplate = new CertificateTemplate();
		BeanUtils.copyProperties(certificateTemplatePage, certificateTemplate);
		CertificateTemplate certificateTemplateEntity = certificateTemplateService.getById(certificateTemplate.getId());
		if(certificateTemplateEntity==null) {
			return Result.error("未找到对应数据");
		}
		certificateTemplateService.updateMain(certificateTemplate, certificateTemplatePage.getEntryDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "凭证模板-通过id删除")
	@ApiOperation(value="凭证模板-通过id删除", notes="凭证模板-通过id删除")
    //@RequiresPermissions("voucher:byx_certificate_template:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		certificateTemplateService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "凭证模板-批量删除")
	@ApiOperation(value="凭证模板-批量删除", notes="凭证模板-批量删除")
    //@RequiresPermissions("voucher:byx_certificate_template:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.certificateTemplateService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "凭证模板-通过id查询")
	@ApiOperation(value="凭证模板-通过id查询", notes="凭证模板-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CertificateTemplate> queryById(@RequestParam(name="id",required=true) String id) {
		CertificateTemplate certificateTemplate = certificateTemplateService.getById(id);
		if(certificateTemplate==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(certificateTemplate);

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
    * @param certificateTemplate
    */
    //@RequiresPermissions("voucher:byx_certificate_template:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CertificateTemplate certificateTemplate) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<CertificateTemplate> queryWrapper = QueryGenerator.initQueryWrapper(certificateTemplate, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

     //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
           List<String> selectionList = Arrays.asList(selections.split(","));
           queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<CertificateTemplate>  certificateTemplateList = certificateTemplateService.list(queryWrapper);

      // Step.3 组装pageList
      List<CertificateTemplatePage> pageList = new ArrayList<CertificateTemplatePage>();
      for (CertificateTemplate main : certificateTemplateList) {
          CertificateTemplatePage vo = new CertificateTemplatePage();
          BeanUtils.copyProperties(main, vo);
          List<EntryDetail> entryDetailList = entryDetailService.selectByMainId(main.getId());
          vo.setEntryDetailList(entryDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "凭证模板列表");
      mv.addObject(NormalExcelConstants.CLASS, CertificateTemplatePage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("凭证模板数据", "导出人:"+sysUser.getRealname(), "凭证模板"));
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
    //@RequiresPermissions("voucher:byx_certificate_template:importExcel")
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
              List<CertificateTemplatePage> list = ExcelImportUtil.importExcel(file.getInputStream(), CertificateTemplatePage.class, params);
              for (CertificateTemplatePage page : list) {
                  CertificateTemplate po = new CertificateTemplate();
                  BeanUtils.copyProperties(page, po);
                  certificateTemplateService.saveMain(po, page.getEntryDetailList());
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
