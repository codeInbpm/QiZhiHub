package com.pig4cloud.pig.oa.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.oa.entity.OaEmployeeCertificatesEntity;
import com.pig4cloud.pig.oa.feign.RemoteEmployeeService;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.pig.oa.entity.OaEmployeeContractsEntity;
import com.pig4cloud.pig.oa.service.OaEmployeeContractsService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * OA员工合同表
 *
 * @author wangben
 * @date 2025-10-22 09:51:33
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/oaEmployeeContracts" )
@Tag(description = "oaEmployeeContracts" , name = "OA员工合同表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class OaEmployeeContractsController {

    private final  OaEmployeeContractsService oaEmployeeContractsService;
	private final RemoteEmployeeService remoteEmployeeService; // Feign 服务注入

    /**
     * 分页查询
     * @param page 分页对象
     * @param oaEmployeeContracts OA员工合同表
     * @return
     */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	@HasPermission("oa_oaEmployeeContracts_view")
	public R<IPage<OaEmployeeContractsEntity>> getOaEmployeeContractsPage(@ParameterObject Page<OaEmployeeContractsEntity> page, @ParameterObject OaEmployeeContractsEntity oaEmployeeContracts) {
		LambdaQueryWrapper<OaEmployeeContractsEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(oaEmployeeContracts.getContractNumber()), OaEmployeeContractsEntity::getContractNumber, oaEmployeeContracts.getContractNumber());
		wrapper.like(StrUtil.isNotBlank(oaEmployeeContracts.getContractName()), OaEmployeeContractsEntity::getContractName, oaEmployeeContracts.getContractName());
		wrapper.like(StrUtil.isNotBlank(oaEmployeeContracts.getContractType()), OaEmployeeContractsEntity::getContractType, oaEmployeeContracts.getContractType());
		wrapper.eq(Objects.nonNull(oaEmployeeContracts.getContractStatus()), OaEmployeeContractsEntity::getContractStatus, oaEmployeeContracts.getContractStatus());

		IPage<OaEmployeeContractsEntity> result = oaEmployeeContractsService.page(page, wrapper);

		// 处理员工姓名：收集 employeeId，调用 Feign 获取姓名，并设置到实体中
		if (result.getRecords() != null && !result.getRecords().isEmpty()) {
			List<Integer> employeeIdList = result.getRecords().stream()
					.map(OaEmployeeContractsEntity::getEmployeeId)
					.filter(Objects::nonNull)
					.distinct()
					.collect(Collectors.toList());

			if (!employeeIdList.isEmpty()) {
				// 转换 Integer 为 Long 以匹配 Feign 接口
				List<Long> longEmployeeIds = employeeIdList.stream()
						.map(Integer::longValue)
						.collect(Collectors.toList());
				R<Map<Long, String>> nameResponse = oaEmployeeContractsService.getEmployeeNamesByIds(longEmployeeIds);
				if (nameResponse != null && !nameResponse.getData().isEmpty() && nameResponse.getData() != null) {
					Map<Long, String> nameMap = nameResponse.getData();
					result.getRecords().forEach(record -> {
						if (record.getEmployeeId() != null) {
							String employeeName = nameMap.get((long) record.getEmployeeId());
							if (employeeName != null) {
								record.setEmployeeName(employeeName); // 设置到实体的新字段
							}
						}
					});
				}
			}
		}
		return R.ok(result);
}


    /**
     * 通过条件查询OA员工合同表
     * @param oaEmployeeContracts 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("oa_oaEmployeeContracts_view")
    public R getDetails(@ParameterObject OaEmployeeContractsEntity oaEmployeeContracts) {
        return R.ok(oaEmployeeContractsService.list(Wrappers.query(oaEmployeeContracts)));
    }

    /**
     * 新增OA员工合同表
     * @param oaEmployeeContracts OA员工合同表
     * @return R
     */
    @Operation(summary = "新增OA员工合同表" , description = "新增OA员工合同表" )
    @SysLog("新增OA员工合同表" )
    @PostMapping
    @HasPermission("oa_oaEmployeeContracts_add")
    public R save(@RequestBody OaEmployeeContractsEntity oaEmployeeContracts) {
        return R.ok(oaEmployeeContractsService.save(oaEmployeeContracts));
    }

    /**
     * 修改OA员工合同表
     * @param oaEmployeeContracts OA员工合同表
     * @return R
     */
    @Operation(summary = "修改OA员工合同表" , description = "修改OA员工合同表" )
    @SysLog("修改OA员工合同表" )
    @PutMapping
    @HasPermission("oa_oaEmployeeContracts_edit")
    public R updateById(@RequestBody OaEmployeeContractsEntity oaEmployeeContracts) {
        return R.ok(oaEmployeeContractsService.updateById(oaEmployeeContracts));
    }

    /**
     * 通过id删除OA员工合同表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除OA员工合同表" , description = "通过id删除OA员工合同表" )
    @SysLog("通过id删除OA员工合同表" )
    @DeleteMapping
    @HasPermission("oa_oaEmployeeContracts_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(oaEmployeeContractsService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param oaEmployeeContracts 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("oa_oaEmployeeContracts_export")
    public List<OaEmployeeContractsEntity> exportExcel(OaEmployeeContractsEntity oaEmployeeContracts,Integer[] ids) {
        return oaEmployeeContractsService.list(Wrappers.lambdaQuery(oaEmployeeContracts).in(ArrayUtil.isNotEmpty(ids), OaEmployeeContractsEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param oaEmployeeContractsList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("oa_oaEmployeeContracts_export")
    public R importExcel(@RequestExcel List<OaEmployeeContractsEntity> oaEmployeeContractsList, BindingResult bindingResult) {
        return R.ok(oaEmployeeContractsService.saveBatch(oaEmployeeContractsList));
    }
}
