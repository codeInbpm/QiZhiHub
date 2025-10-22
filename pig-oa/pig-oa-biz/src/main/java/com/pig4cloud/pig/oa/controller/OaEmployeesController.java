package com.pig4cloud.pig.oa.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.oa.entity.OaEmployeesEntity;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.oa.vo.OaEmployeesVO;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.pig.oa.service.OaEmployeesService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * OA员工档案主表
 *
 * @author wangben
 * @date 2025-10-12 21:59:38
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/oaEmployees" )
@Tag(description = "oaEmployees" , name = "OA员工档案主表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class OaEmployeesController {

    private final  OaEmployeesService oaEmployeesService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param oaEmployees OA员工档案主表
     * @return
     */
	@Operation(summary = "分页查询" , description = "分页查询" )
	@GetMapping("/page" )
	@HasPermission("oa_oaEmployees_view")
	public R getOaEmployeesPage(@ParameterObject Page page, @ParameterObject OaEmployeesEntity oaEmployees) {
		// 查询员工列表
		LambdaQueryWrapper<OaEmployeesEntity> wrapper = Wrappers.<OaEmployeesEntity>lambdaQuery()
				.like(StrUtil.isNotBlank(oaEmployees.getEmployeeNo()), OaEmployeesEntity::getEmployeeNo, oaEmployees.getEmployeeNo());

		IPage<OaEmployeesEntity> employeePage = oaEmployeesService.page(page, wrapper);
		List<OaEmployeesEntity> employees = employeePage.getRecords();

		// 批量获取部门名称
		if (CollUtil.isNotEmpty(employees)) {
			// 收集所有唯一 deptId
			List<Long> deptIds = employees.stream()
					.map(OaEmployeesEntity::getDepartmentId)  // 假设有 getDepartmentId()
					.filter(Objects::nonNull)
					.distinct()
					.collect(Collectors.toList());

			// 一次性调用 Service（委托 Feign）
			R<Map<Long, String>> result = oaEmployeesService.getDeptNamesByIds(deptIds);
			Map<Long, String> deptNameMap = (result != null && result.getData() != null) ? result.getData() : new HashMap<>();

			// 批量设置
			employees.forEach(employee -> {
				Long deptId = employee.getDepartmentId();
				employee.setDeptName((deptId != null) ? deptNameMap.get(deptId) : null);
			});
		}

		return R.ok(employeePage.setRecords(employees));
	}


    /**
     * 通过条件查询OA员工档案主表
     * @param oaEmployees 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("oa_oaEmployees_view")
    public R getDetails(@ParameterObject OaEmployeesEntity oaEmployees) {
        return R.ok(oaEmployeesService.list(Wrappers.query(oaEmployees)));
    }

    /**
     * 新增OA员工档案主表
     * @param oaEmployees OA员工档案主表
     * @return R
     */
    @Operation(summary = "新增OA员工档案主表" , description = "新增OA员工档案主表" )
    @SysLog("新增OA员工档案主表" )
    @PostMapping
    @HasPermission("oa_oaEmployees_add")
    public R save(@RequestBody OaEmployeesEntity oaEmployees) {
        return R.ok(oaEmployeesService.save(oaEmployees));
    }

    /**
     * 修改OA员工档案主表
     * @param oaEmployees OA员工档案主表
     * @return R
     */
    @Operation(summary = "修改OA员工档案主表" , description = "修改OA员工档案主表" )
    @SysLog("修改OA员工档案主表" )
    @PutMapping
    @HasPermission("oa_oaEmployees_edit")
    public R updateById(@RequestBody OaEmployeesEntity oaEmployees) {
        return R.ok(oaEmployeesService.updateById(oaEmployees));
    }

    /**
     * 通过id删除OA员工档案主表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除OA员工档案主表" , description = "通过id删除OA员工档案主表" )
    @SysLog("通过id删除OA员工档案主表" )
    @DeleteMapping
    @HasPermission("oa_oaEmployees_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(oaEmployeesService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param oaEmployees 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("oa_oaEmployees_export")
    public List<OaEmployeesEntity> exportExcel(OaEmployeesEntity oaEmployees,Integer[] ids) {
        return oaEmployeesService.list(Wrappers.lambdaQuery(oaEmployees).in(ArrayUtil.isNotEmpty(ids), OaEmployeesEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param oaEmployeesList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("oa_oaEmployees_export")
    public R importExcel(@RequestExcel List<OaEmployeesVO> oaEmployeesList, BindingResult bindingResult) {
        return R.ok(oaEmployeesService.importEmployees(oaEmployeesList,bindingResult));
    }

    /**
     * 获取员工列表数据（用于下拉选择）
     * @return 员工列表数据
     */
    @Operation(summary = "获取员工列表数据", description = "用于下拉选择")
    @GetMapping("/list")
    public R getEmployeeList() {
        // 查询所有员工基本信息
        List<OaEmployeesEntity> employees = oaEmployeesService.list(
            Wrappers.<OaEmployeesEntity>lambdaQuery()
                .select(OaEmployeesEntity::getId, OaEmployeesEntity::getEnpName, OaEmployeesEntity::getEmployeeNo)
        );
        
        return R.ok(employees);
    }
}
