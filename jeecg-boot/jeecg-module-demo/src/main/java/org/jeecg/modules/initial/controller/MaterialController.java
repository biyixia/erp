package org.jeecg.modules.initial.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.initial.entity.Material;
import org.jeecg.modules.initial.entity.MaterialType;
import org.jeecg.modules.initial.service.IMaterialService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.initial.service.IMaterialTypeService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 物料管理
 * @Author: jeecg-boot
 * @Date:   2023-04-07
 * @Version: V1.0
 */
@Api(tags="物料管理")
@RestController
@RequestMapping("/initial/material")
@Slf4j
public class MaterialController extends JeecgController<Material, IMaterialService> {
	@Autowired
	private IMaterialService materialService;
	 @Autowired
	 private IMaterialTypeService materialTypeService;

	 /**
	  * 物料选择组件 专用  根据物料名称或类型分页查询
	  * @return
	  */
	 @RequestMapping(value = "/queryMaterialComponentData", method = RequestMethod.GET)
	 public Result<IPage<Material>> queryUserComponentData(
			 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
			 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
			 @RequestParam(name = "owningCode", required = false) String owningCode,
			 @RequestParam(name="materialName",required=false) String materialName,
			 @RequestParam(name="id",required = false) String id) {
		 //update-begin-author:taoyan date:2022-7-14 for: VUEN-1702【禁止问题】sql注入漏洞
		 String[] arr = new String[]{owningCode, materialName, id};
		 SqlInjectionUtil.filterContent(arr, SymbolConstant.SINGLE_QUOTATION_MARK);
		 //update-end-author:taoyan date:2022-7-14 for: VUEN-1702【禁止问题】sql注入漏洞
		 IPage<Material> pageList = materialService.queryMaterialPageList(owningCode, materialName, pageSize, pageNo,id);
		 return Result.OK(pageList);
	 }
	
	/**
	 * 分页列表查询
	 *
	 * @param material
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "物料管理-分页列表查询")
	@ApiOperation(value="物料管理-分页列表查询", notes="物料管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Material>> queryPageList(Material material,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Material> queryWrapper = QueryGenerator.initQueryWrapper(material, req.getParameterMap());
		Page<Material> page = new Page<Material>(pageNo, pageSize);
		IPage<Material> pageList = materialService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 分页产品列表查询
	  *
	  * @param material
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "物料管理-分页列表查询")
	 @ApiOperation(value="物料管理-分页产品列表查询", notes="物料管理-分页产品列表查询")
	 @GetMapping(value = "/prod")
	 public Result<IPage<Material>> queryPageProdList(Material material,
												  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												  HttpServletRequest req) {
		 QueryWrapper<Material> queryWrapper = QueryGenerator.initQueryWrapper(material, req.getParameterMap());
		 ArrayList<String> ids = new ArrayList<>();
		 for (MaterialType materialType : materialTypeService.queryTreeListNoPage(new QueryWrapper<MaterialType>().eq("name", "产成品"))) {
			 ids.add(materialType.getId());
			 materialTypeService.getAllChildIds(materialType,ids);
		 }
		 if (ids.size() > 0) {
			 queryWrapper.in("owning_code", ids);
			 Page<Material> page = new Page<Material>(pageNo, pageSize);
			 IPage<Material> pageList = materialService.page(page, queryWrapper);
			 return Result.OK(pageList);
		 } else {
			 return Result.error("您还没有配置产成品相关信息");
		 }
	 }
	
	/**
	 *   添加
	 *
	 * @param material
	 * @return
	 */
	@AutoLog(value = "物料管理-添加")
	@ApiOperation(value="物料管理-添加", notes="物料管理-添加")
	//@RequiresPermissions("initial:byx_material:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Material material) {
		materialService.save(material);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param material
	 * @return
	 */
	@AutoLog(value = "物料管理-编辑")
	@ApiOperation(value="物料管理-编辑", notes="物料管理-编辑")
	//@RequiresPermissions("initial:byx_material:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Material material) {
		System.out.println(material.getRate());
		materialService.updateById(material);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物料管理-通过id删除")
	@ApiOperation(value="物料管理-通过id删除", notes="物料管理-通过id删除")
	//@RequiresPermissions("initial:byx_material:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		materialService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "物料管理-批量删除")
	@ApiOperation(value="物料管理-批量删除", notes="物料管理-批量删除")
	//@RequiresPermissions("initial:byx_material:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.materialService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "物料管理-通过id查询")
	@ApiOperation(value="物料管理-通过id查询", notes="物料管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Material> queryById(@RequestParam(name="id",required=true) String id) {
		Material material = materialService.getById(id);
		if(material==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(material);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param material
    */
    //@RequiresPermissions("initial:byx_material:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Material material) {
        return super.exportXls(request, material, Material.class, "物料管理");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("initial:byx_material:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Material.class);
    }

}
