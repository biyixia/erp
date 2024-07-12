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
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.initial.entity.MaterialType;
import org.jeecg.modules.initial.entity.MaterialTypeTreeModel;
import org.jeecg.modules.initial.service.IMaterialTypeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.model.SysDepartTreeModel;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;


/**
 * @Description: 物料类型
 * @Author: jeecg-boot
 * @Date:   2023-04-06
 * @Version: V1.0
 */
@Api(tags="物料类型")
@RestController
@RequestMapping("/initial/materialType")
@Slf4j
public class MaterialTypeController extends JeecgController<MaterialType, IMaterialTypeService>{
	@Autowired
	private IMaterialTypeService materialTypeService;

	/**
	 * 查询数据 查出所有部门,并以树结构数据格式响应给前端
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public Result<List<MaterialTypeTreeModel>> queryTreeList(@RequestParam(name = "pid", required = false) String ids) {
		Result<List<MaterialTypeTreeModel>> result = new Result<>();
		try {
			if(oConvertUtils.isNotEmpty(ids)){
				List<MaterialTypeTreeModel> materialTypeList = materialTypeService.queryTreeList(ids);
				result.setResult(materialTypeList);
			}else{
				List<MaterialTypeTreeModel> list = materialTypeService.queryTreeList();
				result.setResult(list);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 异步查询物料类型list
	 * @param parentId 父节点 异步加载时传递
	 * @param ids 前端回显是传递
	 * @param primaryKey 主键字段（id或者orgCode）
	 * @return
	 */
	@RequestMapping(value = "/queryMaterialTypeTreeSync", method = RequestMethod.GET)
	public Result<List<MaterialTypeTreeModel>> queryMaterialTypeTreeSync(@RequestParam(name = "pid", required = false) String parentId,@RequestParam(name = "ids", required = false) String ids, @RequestParam(name = "primaryKey", required = false) String primaryKey) {
		Result<List<MaterialTypeTreeModel>> result = new Result<>();
		try {
			List<MaterialTypeTreeModel> list = materialTypeService.queryTreeListByPid(parentId,ids, primaryKey);
			result.setResult(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 分页列表查询
	 *
	 * @param materialType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "物料类型-分页列表查询")
	@ApiOperation(value="物料类型-分页列表查询", notes="物料类型-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<IPage<MaterialType>> queryPageList(MaterialType materialType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
        if(hasQuery != null && "true".equals(hasQuery)){
            QueryWrapper<MaterialType> queryWrapper =  QueryGenerator.initQueryWrapper(materialType, req.getParameterMap());
            List<MaterialType> list = materialTypeService.queryTreeListNoPage(queryWrapper);
            IPage<MaterialType> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        }else{
            String parentId = materialType.getPid();
            if (oConvertUtils.isEmpty(parentId)) {
                parentId = "0";
            }
            materialType.setPid(null);
            QueryWrapper<MaterialType> queryWrapper = QueryGenerator.initQueryWrapper(materialType, req.getParameterMap());
            // 使用 eq 防止模糊查询
            queryWrapper.eq("pid", parentId);
            Page<MaterialType> page = new Page<MaterialType>(pageNo, pageSize);
            IPage<MaterialType> pageList = materialTypeService.page(page, queryWrapper);
            return Result.OK(pageList);
        }
	}

	 /**
	  * 【vue3专用】加载节点的子数据
	  *
	  * @param pid
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeChildren(@RequestParam(name = "pid") String pid) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = materialTypeService.queryListByPid(pid);
			 result.setResult(ls);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setMessage(e.getMessage());
			 result.setSuccess(false);
		 }
		 return result;
	 }

	 /**
	  * 【vue3专用】加载一级节点/如果是同步 则所有数据
	  *
	  * @param async
	  * @param pcode
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeRoot(@RequestParam(name = "async") Boolean async, @RequestParam(name = "pcode") String pcode) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = materialTypeService.queryListByCode(pcode);
			 if (!async) {
				 loadAllChildren(ls);
			 }
			 result.setResult(ls);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setMessage(e.getMessage());
			 result.setSuccess(false);
		 }
		 return result;
	 }

	 /**
	  * 【vue3专用】递归求子节点 同步加载用到
	  *
	  * @param ls
	  */
	 private void loadAllChildren(List<SelectTreeModel> ls) {
		 for (SelectTreeModel tsm : ls) {
			 List<SelectTreeModel> temp = materialTypeService.queryListByPid(tsm.getKey());
			 if (temp != null && temp.size() > 0) {
				 tsm.setChildren(temp);
				 loadAllChildren(temp);
			 }
		 }
	 }

	 /**
      * 获取子数据
      * @param materialType
      * @param req
      * @return
      */
	//@AutoLog(value = "物料类型-获取子数据")
	@ApiOperation(value="物料类型-获取子数据", notes="物料类型-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<MaterialType>> queryPageList(MaterialType materialType,HttpServletRequest req) {
		QueryWrapper<MaterialType> queryWrapper = QueryGenerator.initQueryWrapper(materialType, req.getParameterMap());
		List<MaterialType> list = materialTypeService.list(queryWrapper);
		IPage<MaterialType> pageList = new Page<>(1, 10, list.size());
        pageList.setRecords(list);
		return Result.OK(pageList);
	}

    /**
      * 批量查询子节点
      * @param parentIds 父ID（多个采用半角逗号分割）
      * @return 返回 IPage
      * @param parentIds
      * @return
      */
	//@AutoLog(value = "物料类型-批量获取子数据")
    @ApiOperation(value="物料类型-批量获取子数据", notes="物料类型-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<MaterialType> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<MaterialType> list = materialTypeService.list(queryWrapper);
            IPage<MaterialType> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子节点失败：" + e.getMessage());
        }
    }
	
	/**
	 *   添加
	 *
	 * @param materialType
	 * @return
	 */
	@AutoLog(value = "物料类型-添加")
	@ApiOperation(value="物料类型-添加", notes="物料类型-添加")
    //@RequiresPermissions("initial:byx_material_type:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody MaterialType materialType) {
		materialTypeService.addMaterialType(materialType);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param materialType
	 * @return
	 */
	@AutoLog(value = "物料类型-编辑")
	@ApiOperation(value="物料类型-编辑", notes="物料类型-编辑")
    //@RequiresPermissions("initial:byx_material_type:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody MaterialType materialType) {
		materialTypeService.updateMaterialType(materialType);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物料类型-通过id删除")
	@ApiOperation(value="物料类型-通过id删除", notes="物料类型-通过id删除")
    //@RequiresPermissions("initial:byx_material_type:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		materialTypeService.deleteMaterialType(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "物料类型-批量删除")
	@ApiOperation(value="物料类型-批量删除", notes="物料类型-批量删除")
    //@RequiresPermissions("initial:byx_material_type:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.materialTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "物料类型-通过id查询")
	@ApiOperation(value="物料类型-通过id查询", notes="物料类型-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<MaterialType> queryById(@RequestParam(name="id",required=true) String id) {
		MaterialType materialType = materialTypeService.getById(id);
		if(materialType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(materialType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param materialType
    */
    //@RequiresPermissions("initial:byx_material_type:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MaterialType materialType) {
		return super.exportXls(request, materialType, MaterialType.class, "物料类型");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("initial:byx_material_type:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, MaterialType.class);
    }

}
