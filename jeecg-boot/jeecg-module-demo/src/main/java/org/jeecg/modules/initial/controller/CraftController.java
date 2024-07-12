package org.jeecg.modules.initial.controller;

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
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.initial.entity.Craft;
import org.jeecg.modules.initial.service.ICraftService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

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
 * @Description: 工艺档案
 * @Author: jeecg-boot
 * @Date:   2023-05-03
 * @Version: V1.0
 */
@Api(tags="工艺档案")
@RestController
@RequestMapping("/initial/craft")
@Slf4j
public class CraftController extends JeecgController<Craft, ICraftService> {
	@Autowired
	private ICraftService craftService;
	
	/**
	 * 分页列表查询
	 *
	 * @param craft
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "工艺档案-分页列表查询")
	@ApiOperation(value="工艺档案-分页列表查询", notes="工艺档案-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Craft>> queryPageList(Craft craft,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Craft> queryWrapper = QueryGenerator.initQueryWrapper(craft, req.getParameterMap());
		Page<Craft> page = new Page<Craft>(pageNo, pageSize);
		IPage<Craft> pageList = craftService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param craft
	 * @return
	 */
	@AutoLog(value = "工艺档案-添加")
	@ApiOperation(value="工艺档案-添加", notes="工艺档案-添加")
	//@RequiresPermissions("initial:byx_craft:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Craft craft) {
		craftService.save(craft);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param craft
	 * @return
	 */
	@AutoLog(value = "工艺档案-编辑")
	@ApiOperation(value="工艺档案-编辑", notes="工艺档案-编辑")
	//@RequiresPermissions("initial:byx_craft:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Craft craft) {
		craftService.updateById(craft);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "工艺档案-通过id删除")
	@ApiOperation(value="工艺档案-通过id删除", notes="工艺档案-通过id删除")
	//@RequiresPermissions("initial:byx_craft:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		craftService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "工艺档案-批量删除")
	@ApiOperation(value="工艺档案-批量删除", notes="工艺档案-批量删除")
	//@RequiresPermissions("initial:byx_craft:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.craftService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "工艺档案-通过id查询")
	@ApiOperation(value="工艺档案-通过id查询", notes="工艺档案-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Craft> queryById(@RequestParam(name="id",required=true) String id) {
		Craft craft = craftService.getById(id);
		if(craft==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(craft);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param craft
    */
    //@RequiresPermissions("initial:byx_craft:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Craft craft) {
        return super.exportXls(request, craft, Craft.class, "工艺档案");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("initial:byx_craft:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Craft.class);
    }

}
